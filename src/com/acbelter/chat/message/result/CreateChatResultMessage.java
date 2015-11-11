package com.acbelter.chat.message.result;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

public class CreateChatResultMessage extends Message {
    private Long newChatId;

    public CreateChatResultMessage() {
        setType(CommandType.CREATE_CHAT_RESULT);
    }

    public CreateChatResultMessage(Long newChatId) {
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
        return "CreateChatResultMessage{" +
                "newChatId=" + newChatId +
                "} " + super.toString();
    }
}
