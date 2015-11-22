package com.acbelter.chat.jdbc;

import com.acbelter.chat.message.ChatSendMessage;
import com.acbelter.chat.message.base.Chat;
import com.acbelter.chat.message.base.MessageStore;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

public class MessageDatabaseStore implements MessageStore {
    private Connection connection;
    private QueryExecutor queryExecutor;

    public MessageDatabaseStore() {
        connection = DatabaseConnector.getInstance().getConnection();
        queryExecutor = new QueryExecutor(connection);
    }

    @Override
    public Chat createChat(Set<Long> userIdList) {
        return null;
    }

    @Override
    public List<Long> getChatsByUserId(Long userId) {
        return null;
    }

    @Override
    public Chat getChatById(Long chatId) {
        return null;
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        return null;
    }

    @Override
    public ChatSendMessage getMessageById(Long messageId) {
        return null;
    }

    @Override
    public void addMessage(Long chatId, ChatSendMessage message) {

    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {

    }
}
