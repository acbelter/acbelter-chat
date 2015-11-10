package com.acbelter.chat.message;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

/**
 * /chat_history <chat_id>
 */
public class ChatHistoryMessage extends Message {
    private Long chatId;

    public ChatHistoryMessage() {
        setType(CommandType.CHAT_HISTORY);
    }

    public ChatHistoryMessage(Long chatId) {
        this();
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return "ChatHistoryMessage{" +
                "chatId=" + chatId +
                "} " + super.toString();
    }
}
