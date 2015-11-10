package com.acbelter.chat.message;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

/**
 * /chat_find <chat_id> <regex>
 */
public class ChatFindMessage extends Message {
    private Long chatId;
    private String regex;

    public ChatFindMessage() {
        setType(CommandType.CHAT_FIND);
    }

    public ChatFindMessage(Long chatId, String regex) {
        this();
        this.chatId = chatId;
        this.regex = regex;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String toString() {
        return "ChatFindMessage{" +
                "chatId=" + chatId +
                ", regex='" + regex + '\'' +
                "} " + super.toString();
    }
}
