package com.acbelter.chat.message;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

/**
 * /chat_send <id> <message>
 */
public class ChatSendMessage extends Message {
    private Long chatId;
    private String message;

    public ChatSendMessage() {
        setType(CommandType.CHAT_SEND);
    }

    public ChatSendMessage(Long chatId, String message) {
        this();
        this.chatId = chatId;
        this.message = message;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ChatSendMessage{" +
                "chatId=" + chatId +
                ", message='" + message + '\'' +
                "} " + super.toString();
    }
}
