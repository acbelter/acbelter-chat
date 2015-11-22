package com.acbelter.chat.net;

import com.acbelter.chat.command.base.CommandParser;
import com.acbelter.chat.message.*;
import com.acbelter.chat.message.base.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {
    static Logger log = LoggerFactory.getLogger(MessageBuilder.class);

    public static Message buildMessage(String line) {
        String name = CommandParser.parseName(line);
        if (name == null) {
            return null;
        }

        switch (name) {
            case "chat_create": {
                String[] args = CommandParser.parseArgs(line);
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
                // FIXME Вынести в метод
                String trimLine = line.trim();
                int firstSpaceIndex = trimLine.indexOf(' ');
                int secondSpaceIndex = trimLine.indexOf(' ', firstSpaceIndex + 1);
                if (firstSpaceIndex == -1 || secondSpaceIndex == -1 || secondSpaceIndex == firstSpaceIndex + 1) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                String firstArg = trimLine.substring(firstSpaceIndex + 1, secondSpaceIndex);
                String secondArg = trimLine.substring(secondSpaceIndex + 1, trimLine.length());

                log.info("Arguments for chat_find command: {}, {}", firstArg, secondArg);
                try {
                    return new ChatFindMessage(Long.parseLong(firstArg), secondArg);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            case "chat_history": {
                String[] args = CommandParser.parseArgs(line);
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
                String[] args = CommandParser.parseArgs(line);
                if (args.length != 0) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                return new ChatListMessage();
            }
            case "chat_send": {
                // FIXME Вынести в метод
                String trimLine = line.trim();
                int firstSpaceIndex = trimLine.indexOf(' ');
                int secondSpaceIndex = trimLine.indexOf(' ', firstSpaceIndex + 1);
                if (firstSpaceIndex == -1 || secondSpaceIndex == -1 || secondSpaceIndex == firstSpaceIndex + 1) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                String firstArg = trimLine.substring(firstSpaceIndex + 1, secondSpaceIndex);
                String secondArg = trimLine.substring(secondSpaceIndex + 1, trimLine.length());

                log.info("Arguments for chat_send command: {}, {}", firstArg, secondArg);
                try {
                    return new ChatSendMessage(Long.parseLong(firstArg), secondArg);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            case "help": {
                String[] args = CommandParser.parseArgs(line);
                if (args.length != 0) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                return new HelpMessage();
            }
            case "login": {
                String[] args = CommandParser.parseArgs(line);
                if (args.length == 2) {
                    return new LoginMessage(args[0], args[1]);
                } else if (args.length == 3) {
                    return new LoginMessage(args[0], args[1], args[2]);
                } else {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }
            }
            case "user": {
                String[] args = CommandParser.parseArgs(line);
                if (args.length != 1) {
                    System.out.println("Invalid number of arguments.");
                    return null;
                }

                return new UserMessage(args[0]);
            }
            case "user_info": {
                String[] args = CommandParser.parseArgs(line);
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
                String[] args = CommandParser.parseArgs(line);
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
