package com.acbelter.chat.net.nio;

import com.acbelter.chat.command.*;
import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandHandler;
import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.jdbc.MessageDatabaseStore;
import com.acbelter.chat.jdbc.UserDatabaseStore;
import com.acbelter.chat.message.base.MessageStore;
import com.acbelter.chat.message.base.UserStore;
import com.acbelter.chat.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioServer implements Runnable, Server {
    static Logger log = LoggerFactory.getLogger(NioServer.class);
    private static final int PORT = 9090;

    // Канал, на котором будут приниматься соединения
    private ServerSocketChannel serverChannel;

    // Селектор, который мы будем мониторить
    private Selector selector;

    // Буфер, в который мы будем читать данные, когда они станут доступными
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    // Список запросов на изменение состояния каналов
    private final List<ChangeRequest> changeRequests = new LinkedList<>();

    // Отображение клиентского канала на список буферов, готовых к записи,
    // т.е. у каждого клиента есть свой локальный буфер
    private final Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<>();
    private Map<SocketChannel, ConnectionHandler> handlers = new ConcurrentHashMap<>();

    private ExecutorService executor = Executors.newFixedThreadPool(5);

    private Protocol protocol;
    private SessionManager sessionManager;
    private CommandHandler commandHandler;

    public NioServer(Protocol protocol, SessionManager sessionManager, CommandHandler commandHandler) throws IOException {
        this.protocol = protocol;
        this.sessionManager = sessionManager;
        this.commandHandler = commandHandler;
        selector = initSelector();

        // Слушаем ввод данных с консоли
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("$");
            while (true) {
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    stopServer();
                    return;
                }
            }
        });
        inputThread.start();
    }

    private Selector initSelector() throws IOException {
        // Создаем новый селектор
        Selector socketSelector = Selector.open();

        // Создаем новый канал для работы сервера
        serverChannel = ServerSocketChannel.open();
        // Для работы с селекторами канал должен быть в неблокирующем режиме
        serverChannel.configureBlocking(false);

        // Привязываем серверный сокет к определенному хосту и порту
        InetSocketAddress isa = new InetSocketAddress("localhost", PORT);
        serverChannel.socket().bind(isa);

        // Регистрируем серверный канал в селекторе и указываем, что хотим принимать новые соединения
        serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

        return socketSelector;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Обрабатываем необработанные запросы на изменение состояния каналов
                synchronized(changeRequests) {
                    for (ChangeRequest changeRequest : changeRequests) {
                        switch (changeRequest.type) {
                            case ChangeRequest.CHANGE_OPS:
                                SelectionKey key = changeRequest.socket.keyFor(selector);
                                key.interestOps(changeRequest.ops);
                                break;
                        }
                    }
                    changeRequests.clear();
                }

                // Ожидаем событий на зарегистрированных каналах
                selector.select();

                // Обработка ключей, для которых доступны события
                Iterator selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    // Проверяем, что канал, связанный с ключом готов принять новое соединение и принимаем его
                    if (key.isAcceptable()) {
                        log.info("[acceptable]");
                        accept(key);
                    }
                    // Проверяем, что канал, связанный с ключом готов прочитать данные и читаем их
                    else if (key.isReadable()) {
                        log.info("[readable]");
                        read(key);
                    }
                    // Проверяем, что канал, связанный с ключом готов записать данные и записываем их
                    else if (key.isWritable()) {
                        log.info("[writable]");
                        write(key);
                    }
                }
            } catch (ClosedSelectorException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        // Если канал, связанный с ключом готов принять принять новое подключение, то это серверный канал
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Принимаем соединение и делаем его канал неблокирующим для работы с селектором
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        ConnectionHandler handler = new NioServerHandler(this, socketChannel, sessionManager.createSession(), protocol);
        handler.addListener(commandHandler);
        handlers.put(socketChannel, handler);
        executor.submit(handler);

        // Реристрируем новый канал соединения в селекторе и говорим ему, что хотим узнать,
        // когда в нем появятся данные для чтения
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        // Если канал, связанный с ключом готов принять прочитать данные, то это клиентский канал
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Очищаем буфер для чтения для того чтобы писать туда новые данные
        readBuffer.clear();

        // Пытаемся прочитать данные с канала
        int numRead;
        try {
            numRead = socketChannel.read(readBuffer);
        } catch (IOException e) {
            // Если соединение было принудительно закрыто, то делаем ключ невалидным и закрываем канал
            socketChannel.close();
            key.cancel();
            return;
        }

        if (numRead == -1) {
            // Если соединение было аккуратно закрыто, то делаем ключ невалидным и закрываем канал
            socketChannel.close();
            key.cancel();
            return;
        }

        processClientData(socketChannel, readBuffer.array(), numRead);
    }

    private void processClientData(SocketChannel socketChannel, byte[] data, int numRead) throws IOException {
        // Делаем копию данных перед обработкой
        byte[] receivedData = new byte[numRead];
        System.arraycopy(data, 0, receivedData, 0, numRead);

        ConnectionHandler handler = handlers.get(socketChannel);
        try {
            handler.receive(protocol.decode(receivedData));
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для подготовки данных к записи
     * @param socketChannel Клиентский канал.
     * @param data Данные для записи.
     */
    public void send(SocketChannel socketChannel, byte[] data) {
        synchronized (changeRequests) {
            // Добавляем запрос на изменение состояния канала
            changeRequests.add(new ChangeRequest(socketChannel, ChangeRequest.CHANGE_OPS, SelectionKey.OP_WRITE));

            // Добавляем данные в очередь на запись
            synchronized (pendingData) {
                List<ByteBuffer> queue = pendingData.get(socketChannel);
                if (queue == null) {
                    queue = new ArrayList<>();
                    pendingData.put(socketChannel, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
        }

        // Пробуждаем поток селектора чтобы он мог сделать необходимые изменения
        selector.wakeup();
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (pendingData) {
            // Очередь на запись в конкретный канал
            List<ByteBuffer> queue = pendingData.get(socketChannel);
            // Пишем данные пока они есть
            while (!queue.isEmpty()) {
                ByteBuffer buf = queue.get(0);
                socketChannel.write(buf);
                if (buf.remaining() > 0) {
                    // Если остались незаписанные даннные (т.е. в буфер сокет уже не влезает), то запишем их в следующий раз
                    break;
                }
                queue.remove(0);
            }

            if (queue.isEmpty()) {
                // Мы записали все данные, поэтому теперь будем читать данные
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    @Override
    public void startServer() throws Exception {
        new Thread(this).start();
        log.info("Server started on port {}", PORT);
    }

    @Override
    public void stopServer() {
        for (ConnectionHandler handler: handlers.values()) {
            handler.stop();
        }
    }

    public void removeHandler(SocketChannel channel) {
        handlers.remove(channel);
        log.info("remove handler");
        if (handlers.isEmpty()) {
            shutdownServer();
        }
    }

    public void shutdownServer() {
        try {
            selector.close();
            serverChannel.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Protocol protocol = new ApacheSerializationProtocol();
            SessionManager sessionManager = new SessionManager();

            UserStore userStore = new UserDatabaseStore();
            MessageStore messageStore = new MessageDatabaseStore(userStore);

            Map<CommandType, Command> commands = new HashMap<>();
            commands.put(CommandType.CHAT_CREATE, new ChatCreateCommand(messageStore));
            commands.put(CommandType.CHAT_FIND, new ChatFindCommand(messageStore));
            commands.put(CommandType.CHAT_HISTORY, new ChatHistoryCommand(messageStore));
            commands.put(CommandType.CHAT_LIST, new ChatListCommand(messageStore));
            commands.put(CommandType.CHAT_SEND, new ChatSendCommand(sessionManager, messageStore));
            commands.put(CommandType.HELP, new HelpCommand(commands));
            commands.put(CommandType.LOGIN, new LoginCommand(userStore, sessionManager));
            commands.put(CommandType.USER_INFO, new UserInfoCommand(userStore));
            commands.put(CommandType.USER, new UserCommand(userStore));
            commands.put(CommandType.USER_PASS, new UserPassCommand(userStore));

            CommandHandler handler = new CommandHandler(commands);
            NioServer server = new NioServer(protocol, sessionManager, handler);
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}