package com.acbelter.chat.integration;

import com.acbelter.chat.command.*;
import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandHandler;
import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.jdbc.MessageDatabaseStore;
import com.acbelter.chat.jdbc.UserDatabaseStore;
import com.acbelter.chat.message.LoginMessage;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.MessageStore;
import com.acbelter.chat.message.base.UserStore;
import com.acbelter.chat.message.result.LoginResultMessage;
import com.acbelter.chat.net.*;
import com.acbelter.chat.net.nio.NioClient;
import com.acbelter.chat.net.nio.NioServer;
import com.acbelter.chat.session.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SuccessfulLoginTest implements MessageListener {
    private NioServer server;
    private NioClient client;
    private ConnectionHandler clientHandler;
    private Message resultMessage;

    @Before
    public void setup() throws Exception {
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
            server = new NioServer(protocol, sessionManager, handler);
            server.startServer();

            Thread.sleep(1000);

            try {
                client = new NioClient(null, 9090, protocol, this);
                new Thread(client).start();
                clientHandler = client.getSession().getConnectionHandler();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void successfulLogin() throws Exception {
        clientHandler.send(new LoginMessage("test", "1234"));
        Thread.sleep(1000);
        assert resultMessage.getType() == CommandType.LOGIN_RESULT;
        LoginResultMessage msg = (LoginResultMessage) resultMessage;
        assertEquals(msg.getLogin(), "test");
    }

    @Override
    public void onMessage(Session session, Message message) {
        resultMessage = message;
    }

    @After
    public void close() throws Exception {
//        Thread.sleep(1000);
//        server.stopServer();
    }
}
