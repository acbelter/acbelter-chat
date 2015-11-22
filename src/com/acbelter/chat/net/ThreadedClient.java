package com.acbelter.chat.net;

import com.acbelter.chat.command.base.CommandParser;
import com.acbelter.chat.message.ChatSendMessage;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.result.*;
import com.acbelter.chat.session.Session;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Deprecated
public class ThreadedClient implements MessageListener {
    public static final int PORT = 19000;
    public static final String HOST = "localhost";

    private ConnectionHandler handler;
    private Protocol protocol = new SerializationProtocol();

    public ThreadedClient() {
        init();
    }

    private void init() {
        try {
            Socket socket = new Socket(HOST, PORT);
            Session session = new Session();
            handler = new SocketConnectionHandler(protocol, session, socket);

            // Этот класс будет получать уведомления от socket handler
            handler.addListener(this);

            Thread socketHandler = new Thread(handler);
            socketHandler.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void processInput(String line) throws IOException {
        if (CommandParser.isCommand(line)) {
            Message commandMessage = MessageBuilder.buildMessage(line);
            if (commandMessage != null) {
                handler.send(commandMessage);
            } else {
                System.out.println("Bad command.");
            }
        } else {
            System.out.println("Invalid input: " + line);
        }
    }

    @Override
    public void onMessage(Session session, Message message) {
        switch (message.getType()) {
            case CHAT_CREATE_RESULT: {
                ChatCreateResultMessage chatCreateResultMessage = (ChatCreateResultMessage) message;
                System.out.println("New chat id: " + chatCreateResultMessage.getNewChatId());
                break;
            }
            case CHAT_FIND_RESULT: {
                ChatFindResultMessage chatFindResultMessage = (ChatFindResultMessage) message;
                chatFindResultMessage.getMessages().forEach(System.out::println);
                break;
            }
            case CHAT_HISTORY_RESULT: {
                ChatHistoryResultMessage chatHistoryResultMessage = (ChatHistoryResultMessage) message;
                chatHistoryResultMessage.getMessages().forEach(System.out::println);
                break;
            }
            case CHAT_LIST_RESULT: {
                ChatListResultMessage chatListResultMessage = (ChatListResultMessage) message;
                for (Map.Entry<Long, List<Long>> chatData : chatListResultMessage.getChatData().entrySet()) {
                    System.out.println(chatData.getKey() + ":" + Arrays.toString(chatData.getValue().toArray()));
                }
                break;
            }
            case HELP_RESULT: {
                HelpResultMessage helpResultMessage = (HelpResultMessage) message;
                helpResultMessage.getHelpContent().forEach(System.out::println);
                break;
            }
            case LOGIN_RESULT: {
                LoginResultMessage loginResultMessage = (LoginResultMessage) message;
                User user = new User(loginResultMessage.getLogin());
                user.setId(loginResultMessage.getUserId());
                session.setSessionUser(user);
                System.out.println("Success login. User id: " + user.getId());
                break;
            }
            case USER_INFO_RESULT: {
                UserInfoResultMessage userInfoResultMessage = (UserInfoResultMessage) message;
                User user = userInfoResultMessage.getUser();
                System.out.println("User id: " + user.getId());
                System.out.println("User login: " + user.getLogin());
                System.out.println("User nick: " + user.getNick());
                break;
            }
            case CHAT_SEND: {
                ChatSendMessage chatSendMessage = (ChatSendMessage) message;
                System.out.println(chatSendMessage.getSenderNick() + " (chat_id=" + chatSendMessage.getChatId() +
                        "): " + chatSendMessage.getMessage());
                break;
            }
            case COMMAND_RESULT: {
                CommandResultMessage commandResultMessage = (CommandResultMessage) message;
                System.out.println(commandResultMessage.getData());
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ThreadedClient client = new ThreadedClient();

        Scanner scanner = new Scanner(System.in);
        System.out.println("$");
        while (true) {
            String input = scanner.nextLine();
            if ("q".equals(input)) {
                return;
            }
            client.processInput(input);
        }
    }
}
