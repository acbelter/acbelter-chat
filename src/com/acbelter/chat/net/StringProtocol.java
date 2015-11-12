package com.acbelter.chat.net;

import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.*;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.result.CommandResultMessage;
import com.acbelter.chat.message.result.HelpResultMessage;
import com.acbelter.chat.message.result.LoginResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class StringProtocol implements Protocol {
    static Logger log = LoggerFactory.getLogger(StringProtocol.class);

    public static final String DELIMITER = ";";

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        String str = new String(bytes);
        log.info("decoded: {}", str);
        String[] tokens = str.split(DELIMITER);
        CommandType type = CommandType.valueOf(tokens[0]);
        Long id;
        if (tokens[1].equals("null")) {
            id = null;
        } else {
            id = Long.parseLong(tokens[1]);
        }

        Long senderId;
        if (tokens[2].equals("null")) {
            senderId = null;
        } else {
            senderId = Long.parseLong(tokens[2]);
        }

        final int startIndex = 3;
        switch (type) {
            case CHAT_CREATE: {
                ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                chatCreateMessage.setId(id);
                chatCreateMessage.setSender(senderId);
                for (int i = startIndex; i < tokens.length; i++) {
                    try {
                        chatCreateMessage.addUserId(Long.parseLong(tokens[i]));
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
                return chatCreateMessage;
            }
            case CHAT_FIND: {
                ChatFindMessage chatFindMessage = new ChatFindMessage();
                chatFindMessage.setId(id);
                chatFindMessage.setSender(senderId);
                chatFindMessage.setChatId(Long.parseLong(tokens[3]));
                chatFindMessage.setRegex(tokens[4]);
                return chatFindMessage;
            }
            case CHAT_HISTORY: {
                ChatHistoryMessage chatHistoryMessage = new ChatHistoryMessage();
                chatHistoryMessage.setId(id);
                chatHistoryMessage.setSender(senderId);
                chatHistoryMessage.setChatId(Long.parseLong(tokens[3]));
                return chatHistoryMessage;
            }
            case CHAT_LIST: {
                ChatListMessage chatListMessage = new ChatListMessage();
                chatListMessage.setId(id);
                chatListMessage.setSender(senderId);
                return chatListMessage;
            }
            case CHAT_SEND: {
                ChatSendMessage chatSendMessage = new ChatSendMessage();
                chatSendMessage.setId(id);
                chatSendMessage.setSender(senderId);
                chatSendMessage.setChatId(Long.parseLong(tokens[3]));
                chatSendMessage.setMessage(tokens[4]);
                return chatSendMessage;
            }
            case HELP: {
                HelpMessage helpMessage = new HelpMessage();
                helpMessage.setId(id);
                helpMessage.setSender(senderId);
                return helpMessage;
            }
            case LOGIN: {
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setId(id);
                loginMessage.setSender(senderId);
                loginMessage.setLogin(tokens[3]);
                loginMessage.setPassword(tokens[4]);
                return loginMessage;
            }
            case USER_INFO: {
                UserInfoMessage userInfoMessage = new UserInfoMessage();
                userInfoMessage.setId(id);
                userInfoMessage.setSender(senderId);
                userInfoMessage.setUserId(Long.parseLong(tokens[3]));
                return userInfoMessage;
            }
            case USER: {
                UserMessage userMessage = new UserMessage();
                userMessage.setId(id);
                userMessage.setSender(senderId);
                userMessage.setNick(tokens[3]);
                return userMessage;
            }
            case USER_PASS: {
                UserPassMessage userPassMessage = new UserPassMessage();
                userPassMessage.setId(id);
                userPassMessage.setSender(senderId);
                userPassMessage.setOldPassword(tokens[3]);
                userPassMessage.setNewPassword(tokens[4]);
                return userPassMessage;
            }
            case COMMAND_RESULT: {
                CommandResultMessage resultMessage = new CommandResultMessage();
                resultMessage.setId(id);
                resultMessage.setSender(senderId);
                resultMessage.setState(CommandResultState.valueOf(tokens[3]));
                resultMessage.setData(tokens[4]);
                return resultMessage;
            }
            case LOGIN_RESULT: {
                LoginResultMessage loginResultMessage = new LoginResultMessage();
                loginResultMessage.setId(id);
                loginResultMessage.setSender(senderId);
                loginResultMessage.setLogin(tokens[3]);
                loginResultMessage.setUserId(Long.parseLong(tokens[4]));
                return loginResultMessage;
            }
            case HELP_RESULT: {
                HelpResultMessage helpResultMessage = new HelpResultMessage();
                helpResultMessage.setId(id);
                helpResultMessage.setSender(senderId);
                // FIXME Set help content
            }
            default: {
                throw new ProtocolException("Invalid type: " + type);
            }
        }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        StringBuilder builder = new StringBuilder();
        CommandType type = msg.getType();
        builder.append(type).append(DELIMITER);
        builder.append(msg.getId()).append(DELIMITER);
        builder.append(msg.getSender()).append(DELIMITER);
        switch (type) {
            case CHAT_CREATE: {
                ChatCreateMessage chatCreateMessage = (ChatCreateMessage) msg;
                for (Long id : chatCreateMessage.getUserIds()) {
                    builder.append(id).append(DELIMITER);
                }
                break;
            }
            case CHAT_FIND: {
                ChatFindMessage chatFindMessage = (ChatFindMessage) msg;
                builder.append(chatFindMessage.getChatId()).append(DELIMITER);
                builder.append(chatFindMessage.getRegex()).append(DELIMITER);
                break;
            }
            case CHAT_HISTORY: {
                ChatHistoryMessage chatHistoryMessage = (ChatHistoryMessage) msg;
                builder.append(chatHistoryMessage.getChatId()).append(DELIMITER);
                break;
            }
            case CHAT_LIST: {
                ChatListMessage chatListMessage = (ChatListMessage) msg;
                break;
            }
            case CHAT_SEND: {
                ChatSendMessage chatSendMessage = (ChatSendMessage) msg;
                builder.append(chatSendMessage.getChatId()).append(DELIMITER);
                builder.append(chatSendMessage.getMessage()).append(DELIMITER);
                break;
            }
            case HELP: {
                HelpMessage helpMessage = (HelpMessage) msg;
                break;
            }
            case LOGIN: {
                LoginMessage loginMessage = (LoginMessage) msg;
                builder.append(loginMessage.getLogin()).append(DELIMITER);
                builder.append(loginMessage.getPassword()).append(DELIMITER);
                break;
            }
            case USER_INFO: {
                UserInfoMessage userInfoMessage = (UserInfoMessage) msg;
                builder.append(userInfoMessage.getUserId()).append(DELIMITER);
                break;
            }
            case USER: {
                UserMessage userMessage = (UserMessage) msg;
                builder.append(userMessage.getNick()).append(DELIMITER);
                break;
            }
            case USER_PASS: {
                UserPassMessage userPassMessage = (UserPassMessage) msg;
                builder.append(userPassMessage.getOldPassword()).append(DELIMITER);
                builder.append(userPassMessage.getNewPassword()).append(DELIMITER);
                break;
            }
            case COMMAND_RESULT: {
                CommandResultMessage commandResultMessage = (CommandResultMessage) msg;
                builder.append(commandResultMessage.getState()).append(DELIMITER);
                builder.append(commandResultMessage.getData()).append(DELIMITER);
                break;
            }
            case LOGIN_RESULT: {
                LoginResultMessage loginResultMessage = (LoginResultMessage) msg;
                builder.append(loginResultMessage.getLogin()).append(DELIMITER);
                builder.append(loginResultMessage.getUserId()).append(DELIMITER);
                break;
            }
            case HELP_RESULT: {
                HelpResultMessage helpResultMessage = (HelpResultMessage) msg;
                // FIXME Get help content
                break;
            }
            default: {
                throw new ProtocolException("Invalid type: " + type);
            }
        }
        log.info("encoded: {}", builder.toString());
        return builder.toString().getBytes();
    }
}
