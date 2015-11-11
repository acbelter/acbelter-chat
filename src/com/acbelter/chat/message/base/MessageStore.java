package com.acbelter.chat.message.base;

import com.acbelter.chat.message.ChatSendMessage;

import java.util.List;
import java.util.Set;

public interface MessageStore {
    Chat createChat(Set<Long> userIdList);
    List<Long> getChatsByUserId(Long userId);
    Chat getChatById(Long chatId);
    List<Long> getMessagesFromChat(Long chatId);
    ChatSendMessage getMessageById(Long messageId);
    void addMessage(Long chatId, ChatSendMessage message);
    void addUserToChat(Long userId, Long chatId);
}
