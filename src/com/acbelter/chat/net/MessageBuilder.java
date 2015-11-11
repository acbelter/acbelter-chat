package com.acbelter.chat.net;

import com.acbelter.chat.message.*;
import com.acbelter.chat.message.base.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageBuilder {
    static Logger log = LoggerFactory.getLogger(ThreadedClient.class);

    public static Message buildMessage(String name, String[] args) {
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
}
