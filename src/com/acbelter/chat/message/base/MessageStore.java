package com.acbelter.chat.message.base;

import java.util.List;
import java.util.Set;

public interface MessageStore {
    Chat createChat(Set<Long> userIdList);
    List<Long> getChatsByUserId(Long userId);
    Chat getChatById(Long chatId);
    List<Long> getMessagesFromChat(Long chatId);
    Message getMessageById(Long messageId);
    void addMessage(Long chatId, Message message);
    void addUserToChat(Long userId, Long chatId);
}
