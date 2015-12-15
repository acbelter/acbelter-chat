package com.acbelter.chat.net;

import com.acbelter.chat.command.*;
import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandHandler;
import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.jdbc.MessageDatabaseStore;
import com.acbelter.chat.jdbc.UserDatabaseStore;
import com.acbelter.chat.message.base.MessageStore;
import com.acbelter.chat.message.base.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

@Deprecated
public class ThreadedServer implements Server {
    public static final int PORT = 19000;
    static Logger log = LoggerFactory.getLogger(ThreadedServer.class);
    private volatile boolean isRunning;
    private Map<Long, ConnectionHandler> handlers = new HashMap<>();
    private AtomicLong internalCounter = new AtomicLong(0);
    private ServerSocket serverSocket;
    private Protocol protocol;
    private SessionManager sessionManager;
    private CommandHandler commandHandler;

    private Map<ConnectionHandler, Thread> handlerThreadMap = new HashMap<>();

    public ThreadedServer(Protocol protocol, SessionManager sessionManager, CommandHandler commandHandler) {
        try {
            this.protocol = protocol;
            this.sessionManager = sessionManager;
            this.commandHandler = commandHandler;
            serverSocket = new ServerSocket(PORT);
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void startServer() throws Exception {
        log.info("Started, waiting for connection");

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

        isRunning = true;
        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                log.info("Accepted. " + socket.getInetAddress());

                ConnectionHandler handler = new SocketConnectionHandler(protocol, sessionManager.createSession(), socket);
                handler.addListener(commandHandler);

                handlers.put(internalCounter.incrementAndGet(), handler);
                Thread thread = new Thread(handler);
                handlerThreadMap.put(handler, thread);
                thread.start();
            } catch (SocketException e) {
                break;
            }
        }
    }

    @Override
    public void stopServer() {
        isRunning = false;
        for (ConnectionHandler handler : handlers.values()) {
            handler.stop();
            try {
                handlerThreadMap.get(handler).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Protocol protocol = new SerializationProtocol();
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
        ThreadedServer server = new ThreadedServer(protocol, sessionManager, handler);

        try {
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

