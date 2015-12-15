package com.acbelter.chat.net.nio;

import com.acbelter.chat.command.base.CommandParser;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.net.*;
import com.acbelter.chat.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NioClient implements Runnable {
    static Logger log = LoggerFactory.getLogger(NioClient.class);

    private InetAddress hostAddress;
    private int port;

    private Protocol protocol;
    private SocketChannel socketChannel;
    private ConnectionHandler handler;

    // Селектор, который мы будем мониторить
    private Selector selector;

    // Буфер, в который мы будем читать данные, когда они станут доступными
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    // Список запросов на изменение состояния каналов
    private final List<ChangeRequest> changeRequests = new LinkedList<>();

    // Отображение клиентского канала на список буферов, готовых к записи,
    // т.е. у каждого клиента есть свой локальный буфер
    private final Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<>();

    private MessageListener messageListener = new NioClientMessageListener();

    public NioClient(InetAddress hostAddress, int port, Protocol protocol) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.protocol = protocol;
        selector = Selector.open();
        socketChannel = initiateConnection();

        Session session = new Session();
        handler = new NioClientHandler(this, session, protocol);
        handler.addListener(messageListener);

        // Слушаем ввод данных с консоли
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("$");
            while (true) {
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    System.exit(0);
                    return;
                }

                if (CommandParser.isCommand(input)) {
                    Message commandMessage = MessageBuilder.buildMessage(input);
                    if (commandMessage != null) {
                        try {
                            handler.send(commandMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Bad command.");
                    }
                } else {
                    System.out.println("Invalid input: " + input);
                }
            }


        });
        inputThread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Обрабатываем необработанные запросы на изменение состояния каналов
                synchronized(changeRequests) {
                    for (ChangeRequest changeRequest : changeRequests) {
                        switch (changeRequest.type) {
                            case ChangeRequest.CHANGE_OPS:
                                // FIXME Иногда key null
                                SelectionKey key = changeRequest.socket.keyFor(selector);
                                if (key != null) {
                                    key.interestOps(changeRequest.ops);
                                }
                                break;
                            case ChangeRequest.REGISTER:
                                changeRequest.socket.register(selector, changeRequest.ops);
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

                    // Проверяем, что канал, связанный с ключом готов установить новое соединение и устанавливаем его
                    if (key.isConnectable()) {
                        log.info("[connectable]");
                        connect(key);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SocketChannel initiateConnection() throws IOException {
        // Создаем неблокирующий канал
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        // Начинаем устанавливать соединение с сервером
        socketChannel.connect(new InetSocketAddress("localhost", port));

        // Добавляем запрос на изменение состояния канала
        // (т.е. делаем запрос на то, чтобы селектор реагировал на установленные подключения)
        synchronized(changeRequests) {
            changeRequests.add(new ChangeRequest(socketChannel, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
        }

        return socketChannel;
    }

    private void connect(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Завершаем установку соединения
        try {
            socketChannel.finishConnect();
        } catch (IOException e) {
            e.printStackTrace();
            // Отменяем регистрацию канала в селекторе
            socketChannel.close();
            key.cancel();
            return;
        }

        // Теперь хотим писать данные на сервер
        key.interestOps(SelectionKey.OP_WRITE);
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

        processServerData(socketChannel, readBuffer.array(), numRead);
    }

    private void processServerData(SocketChannel socketChannel, byte[] data, int numRead) throws IOException {
        // Делаем копию данных перед обработкой
        byte[] receivedData = new byte[numRead];
        System.arraycopy(data, 0, receivedData, 0, numRead);

        try {
            Message message = protocol.decode(receivedData);
            handler.receive(message);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для подготовки данных к записи
     * @param data Данные для записи.
     */
    public void send(byte[] data) {
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
            // FIXME Почему-то в начале очередь null
            if (queue == null) {
                key.interestOps(SelectionKey.OP_READ);
                return;
            }
            // Пишем данные пока они есть
            while (!queue.isEmpty()) {
                ByteBuffer buf = queue.get(0);
                socketChannel.write(buf);
                if (buf.remaining() > 0) {
                    // Если остались незаписанные даннные (т.е. в буфер сокет уже не влезает), то запишем их в седующий раз
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

    public static void main(String[] args) {
        try {
            Protocol protocol = new ApacheSerializationProtocol();
            new Thread(new NioClient(null, 9090, protocol)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}