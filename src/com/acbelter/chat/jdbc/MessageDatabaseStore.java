package com.acbelter.chat.jdbc;

import com.acbelter.chat.message.ChatSendMessage;
import com.acbelter.chat.message.base.Chat;
import com.acbelter.chat.message.base.MessageStore;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class MessageDatabaseStore implements MessageStore {
    static Logger log = LoggerFactory.getLogger(MessageDatabaseStore.class);
    private Connection connection;
    private QueryExecutor queryExecutor;

    public MessageDatabaseStore() {
        connection = DatabaseConnector.getInstance().getConnection();
        queryExecutor = new QueryExecutor(connection);
    }

    @Override
    public Chat createChat(Set<Long> userIdList) {
        if (userIdList == null || userIdList.isEmpty()) {
            return null;
        }

        try {
            List<Long> chatIds = queryExecutor.execUpdate("INSERT INTO chat_table () values ();");
            Chat newChat = new Chat();
            if (chatIds.size() == 1) {
                newChat.setId(chatIds.get(0));
                log.info("Add chat id={} to db", newChat.getId());
            }

            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO chat_user_table (chat_id, user_id) values ");

            for (Long id : userIdList) {
                sb.append("(").append(newChat.getId()).append(",").append(id).append("),");
            }

            sb.deleteCharAt(sb.length() - 1);
            sb.append(";");

            List<Long> ids = queryExecutor.execUpdate(sb.toString());
            if (!ids.isEmpty()) {
                newChat.addParticipants(userIdList);
                log.info("Add {} users to chat id={} to db" , ids.size(), newChat.getId());
                return newChat;
            }

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Long> getChatsByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, userId);

        try {
            List<Long> chatIds = queryExecutor.execQuery("SELECT * FROM chat_user_table WHERE user_id = ?;", prepared, (r) -> {
                List<Long> data = new ArrayList<>();
                while (r.next()) {
                    data.add(r.getLong(1));
                }
                return data;
            });
            log.info("Get chats by user id={} from db", userId);
            return chatIds;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Chat getChatById(Long chatId) {
        if (chatId == null) {
            return null;
        }

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chatId);

        try {
            Chat chat = queryExecutor.execQuery("SELECT * FROM chat_table WHERE id = ? LIMIT 1;", prepared, (r) -> {
                if (r.next()) {
                    Chat c = new Chat();
                    c.setId(r.getLong(1));
                    return c;
                }
                return null;
            });

            List<Long> participants = queryExecutor.execQuery("SELECT * FROM chat_user_table WHERE chat_id = ?;", prepared, (r) -> {
                List<Long> data = new ArrayList<>();
                while (r.next()) {
                    data.add(r.getLong(2));
                }
                return data;
            });
            chat.setParticipantIds(participants);

            List<Long> messages = getMessagesFromChat(chatId);
            chat.setMessageIds(messages);

            log.info("Get chat by id={} from db", chatId);

            return chat;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        if (chatId == null) {
            return new ArrayList<>();
        }


        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chatId);

        try {
            List<Long> messages = queryExecutor.execQuery("SELECT * FROM message_table WHERE chat_id = ?;", prepared, (r) -> {
                List<Long> data = new ArrayList<>();
                while (r.next()) {
                    data.add(r.getLong(1));
                }
                return data;
            });

            log.info("Get messages from chat id={} from db", chatId);

            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ChatSendMessage getMessageById(Long messageId) {
        if (messageId == null) {
            return null;
        }

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, messageId);

        try {
            ChatSendMessage message = queryExecutor.execQuery("SELECT * FROM message_table WHERE id = ? LIMIT 1;", prepared, (r) -> {
                if (r.next()) {
                    ChatSendMessage csm = new ChatSendMessage(r.getLong(3), r.getString(4));
                    csm.setId(r.getLong(1));
                    csm.setSender(r.getLong(2));
                    csm.setSenderNick(r.getString(5));
                    return csm;
                }
                return null;
            });

            log.info("Get message {} from db", message);

            return message;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addMessage(Long chatId, ChatSendMessage message) {
        if (chatId == null || message == null) {
            return false;
        }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO user_table (sender_id, chat_id, message, sender_nick) values ('")
                    .append(message.getSender())
                    .append("', '")
                    .append(message.getChatId())
                    .append("', '")
                    .append(StringEscapeUtils.escapeSql(message.getMessage()))
                    .append("', '")
                    .append(StringEscapeUtils.escapeSql(message.getSenderNick()))
                    .append("');");
            List<Long> ids = queryExecutor.execUpdate(sb.toString());
            if (ids.size() == 1) {
                log.info("Add message to db " + message);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean addUserToChat(Long userId, Long chatId) {
        if (userId == null || chatId == null) {
            return false;
        }

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, chatId);
        prepared.put(2, userId);

        try {
            List<Long> ids = queryExecutor.execUpdate("INSERT INTO chat_user_table (chat_id, user_id) values (?, ?);", prepared);
            if (!ids.isEmpty()) {
                log.info("Add user id={} to chat id={}", userId, chatId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
