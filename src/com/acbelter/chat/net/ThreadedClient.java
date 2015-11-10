package com.acbelter.chat.net;

import com.acbelter.chat.command.base.CommandParser;
import com.acbelter.chat.message.*;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.result.HelpResultMessage;
import com.acbelter.chat.message.result.LoginResultMessage;
import com.acbelter.chat.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ThreadedClient implements MessageListener {
    public static final int PORT = 19000;
    public static final String HOST = "localhost";
    static Logger log = LoggerFactory.getLogger(ThreadedClient.class);
    ConnectionHandler handler;

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
            String name = CommandParser.parseName(line);
            String[] args = CommandParser.parseArgs(line);

            Message commandMessage = buildMessage(name, args);
            if (commandMessage != null) {
                handler.send(commandMessage);
            } else {
                System.out.println("Bad command");
            }
        } else {
            System.out.println("Invalid input: " + line);
        }
    }

    private Message buildMessage(String name, String[] args) {
        log.info("Command: " + name + " args: " + Arrays.toString(args));
        switch (name) {
            case "chat_create": {
                if (args.length == 0) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                List<Long> userIdList = new ArrayList<>();
                for (String arg : args) {
                    try {
                        userIdList.add(Long.parseLong(arg));
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }

                return new ChatCreateMessage(userIdList);
            }
            case "chat_find": {
                if (args.length != 2) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                try {
                    return new ChatFindMessage(Long.parseLong(args[0]), args[1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            case "chat_history": {
                if (args.length != 1) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                try {
                    return new ChatHistoryMessage(Long.parseLong(args[0]));
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            case "chat_list": {
                if (args.length != 0) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                return new ChatListMessage();
            }
            case "chat_send": {
                // TODO При парсинге сообщения из нескольких слов будет неправильное количество аргументоа
                if (args.length != 2) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                try {
                    return new ChatSendMessage(Long.parseLong(args[0]), args[1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            case "help": {
                if (args.length != 0) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                return new HelpMessage();
            }
            case "login": {
                if (args.length != 2) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                return new LoginMessage(args[0], args[1]);
            }
            case "user": {
                if (args.length != 1) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                return new UserMessage(args[0]);
            }
            case "user_info": {
                if (args.length > 1) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                if (args.length == 0) {
                    return new UserInfoMessage();
                } else if (args.length == 1) {
                    try {
                        return new UserInfoMessage(Long.parseLong(args[0]));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
            case "user_pass": {
                if (args.length != 2) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                return new UserPassMessage(args[0], args[1]);
            }
            default: {
                System.out.println("Unknown command: " + name);
                return null;
            }
        }
    }

    @Override
    public void onMessage(Session session, Message msg) {
        System.out.println("SESSION " + session.getSessionUser() + " MESSAGE " + msg);
        switch (msg.getType()) {
            case LOGIN_RESULT: {
                LoginResultMessage loginResultMessage = (LoginResultMessage) msg;
                User user = new User(loginResultMessage.getLogin());
                user.setId(loginResultMessage.getUserId());
                session.setSessionUser(user);
                System.out.println("Success login. User id: " + user.getId());
                break;
            }
            case HELP_RESULT: {
                HelpResultMessage helpResultMessage = (HelpResultMessage) msg;
                System.out.println(helpResultMessage.getContent());
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
