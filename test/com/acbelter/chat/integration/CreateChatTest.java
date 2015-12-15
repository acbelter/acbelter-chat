package com.acbelter.chat.integration;

import com.acbelter.chat.command.*;
import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandHandler;
import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.jdbc.MessageDatabaseStore;
import com.acbelter.chat.jdbc.UserDatabaseStore;
import com.acbelter.chat.message.ChatCreateMessage;
import com.acbelter.chat.message.ChatListMessage;
import com.acbelter.chat.message.LoginMessage;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.MessageStore;
import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.base.UserStore;
import com.acbelter.chat.message.result.ChatCreateResultMessage;
import com.acbelter.chat.message.result.ChatListResultMessage;
import com.acbelter.chat.message.result.LoginResultMessage;
import com.acbelter.chat.net.*;
import com.acbelter.chat.net.nio.NioClient;
import com.acbelter.chat.net.nio.NioServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CreateChatTest {
    private NioServer server;
    private NioClient firstClient;
    private NioClient secondClient;
    private ConnectionHandler firstClientHandler;
    private ConnectionHandler secondClientHandler;

    private MessageListener firstMessageListener;
    private MessageListener secondMessageListener;
    private Message firstResultMessage;
    private Message secondResultMessage;

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
                firstMessageListener = (session, message) -> {
                    if (message.getType() == CommandType.LOGIN_RESULT) {
                        LoginResultMessage loginResultMessage = (LoginResultMessage) message;
                        User user = new User(loginResultMessage.getLogin());
                        user.setId(loginResultMessage.getUserId());
                        session.setSessionUser(user);
                    }
                    firstResultMessage = message;
                };
                firstClient = new NioClient(null, 9090, protocol, firstMessageListener);
                new Thread(firstClient).start();
                firstClientHandler = firstClient.getSession().getConnectionHandler();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Thread.sleep(1000);

            try {
                secondMessageListener = (session, message) -> {
                    if (message.getType() == CommandType.LOGIN_RESULT) {
                        LoginResultMessage loginResultMessage = (LoginResultMessage) message;
                        User user = new User(loginResultMessage.getLogin());
                        user.setId(loginResultMessage.getUserId());
                        session.setSessionUser(user);
                    }
                    secondResultMessage = message;
                };
                secondClient = new NioClient(null, 9090, protocol, secondMessageListener);
                new Thread(secondClient).start();
                secondClientHandler = secondClient.getSession().getConnectionHandler();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createChat() throws Exception {
        firstClientHandler.send(new LoginMessage("test", "1234"));
        Thread.sleep(1000);
        assert firstResultMessage.getType() == CommandType.LOGIN_RESULT;
        LoginResultMessage firstLoginMsg = (LoginResultMessage) firstResultMessage;
        assertEquals(firstLoginMsg.getLogin(), "test");

        firstClientHandler.send(new ChatCreateMessage(firstLoginMsg.getUserId()));
        Thread.sleep(1000);

        secondClientHandler.send(new LoginMessage("user", "123"));
        Thread.sleep(1000);
        assert secondResultMessage.getType() == CommandType.LOGIN_RESULT;
        LoginResultMessage secondLoginMsg = (LoginResultMessage) secondResultMessage;
        assertEquals(secondLoginMsg.getLogin(), "user");

        List<Long> userIdList = new ArrayList<>(2);
        userIdList.add(firstLoginMsg.getUserId());
        userIdList.add(secondLoginMsg.getUserId());

        firstClientHandler.send(new ChatCreateMessage(userIdList));
        Thread.sleep(1000);

        assert firstResultMessage.getType() == CommandType.CHAT_CREATE_RESULT;
        ChatCreateResultMessage chatCreateMsg = (ChatCreateResultMessage) firstResultMessage;

        ChatListMessage getChatListMsg = new ChatListMessage();
        getChatListMsg.setSender(secondLoginMsg.getUserId());
        secondClientHandler.send(new ChatListMessage());
        Thread.sleep(5000);

        assert secondResultMessage.getType() == CommandType.CHAT_LIST_RESULT;
        ChatListResultMessage chatListMsg = (ChatListResultMessage) secondResultMessage;
        assert chatListMsg.getChatData().containsKey(chatCreateMsg.getNewChatId());
        assert chatListMsg.getChatData().get(chatCreateMsg.getNewChatId()).containsAll(userIdList);
    }

    @After
    public void close() throws Exception {
//        Thread.sleep(1000);
//        server.stopServer();
    }
}