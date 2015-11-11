package com.acbelter.chat.net;

import com.acbelter.chat.command.*;
import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandHandler;
import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.MessageStore;
import com.acbelter.chat.message.base.MessageStoreStub;
import com.acbelter.chat.message.base.UserStore;
import com.acbelter.chat.message.base.UserStoreStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadedServer {
    public static final int PORT = 19000;
    static Logger log = LoggerFactory.getLogger(ThreadedServer.class);
    private volatile boolean isRunning;
    private Map<Long, ConnectionHandler> handlers = new HashMap<>();
    private AtomicLong internalCounter = new AtomicLong(0);
    private ServerSocket serverSocket;
    private Protocol protocol;
    private SessionManager sessionManager;
    private CommandHandler commandHandler;

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

    private void startServer() throws Exception {
        log.info("Started, waiting for connection");

        isRunning = true;
        while (isRunning) {
            Socket socket = serverSocket.accept();
            log.info("Accepted. " + socket.getInetAddress());

            ConnectionHandler handler = new SocketConnectionHandler(protocol, sessionManager.createSession(), socket);
            handler.addListener(commandHandler);

            handlers.put(internalCounter.incrementAndGet(), handler);
            Thread thread = new Thread(handler);
            thread.start();
        }
    }

    public void stopServer() {
        isRunning = false;
        for (ConnectionHandler handler : handlers.values()) {
            handler.stop();
        }
    }

    public static void main(String[] args) {
        Protocol protocol = new SerializationProtocol();
        SessionManager sessionManager = new SessionManager();

        UserStore userStore = new UserStoreStub();
        MessageStore messageStore = new MessageStoreStub();

        Map<CommandType, Command> commands = new HashMap<>();
        commands.put(CommandType.CHAT_CREATE, new ChatCreateCommand(messageStore));
        commands.put(CommandType.CHAT_FIND, new ChatFindCommand());
        commands.put(CommandType.CHAT_HISTORY, new ChatHistoryCommand());
        commands.put(CommandType.CHAT_LIST, new ChatListCommand());
        commands.put(CommandType.CHAT_SEND, new ChatSendCommand(sessionManager, messageStore));
        commands.put(CommandType.HELP, new HelpCommand(commands));
        commands.put(CommandType.LOGIN, new LoginCommand(userStore, sessionManager));
        commands.put(CommandType.USER_INFO, new UserInfoCommand());
        commands.put(CommandType.USER, new UserCommand());
        commands.put(CommandType.USER_PASS, new UserPassCommand());

        CommandHandler handler = new CommandHandler(commands);
        ThreadedServer server = new ThreadedServer(protocol, sessionManager, handler);

        try {
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

