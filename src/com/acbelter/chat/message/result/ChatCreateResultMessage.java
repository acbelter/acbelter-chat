package com.acbelter.chat.message.result;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

public class ChatCreateResultMessage extends Message {
    private Long newChatId;

    public ChatCreateResultMessage() {
        setType(CommandType.CHAT_CREATE_RESULT);
    }

    public ChatCreateResultMessage(Long newChatId) {
        this();
        this.newChatId = newChatId;
    }

    public Long getNewChatId() {
        return newChatId;
    }

    public void setNewChatId(Long newChatId) {
        this.newChatId = newChatId;
    }

    @Override
    public String toString() {
        return "ChatCreateResultMessage{" +
                "newChatId=" + newChatId +
                "} " + super.toString();
    }
}
