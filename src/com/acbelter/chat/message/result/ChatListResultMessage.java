package com.acbelter.chat.message.result;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

import java.util.List;
import java.util.Map;

public class ChatListResultMessage extends Message {
    private Map<Long, List<Long>> chatData;

    public ChatListResultMessage() {
        setType(CommandType.CHAT_LIST_RESULT);
    }

    public ChatListResultMessage(Map<Long, List<Long>> chatData) {
        this();
        this.chatData = chatData;
    }

    public Map<Long, List<Long>> getChatData() {
        return chatData;
    }

    public void setChatData(Map<Long, List<Long>> chatData) {
        this.chatData = chatData;
    }

    @Override
    public String toString() {
        return "ChatListResultMessage{" +
                "chatData=" + chatData +
                "} " + super.toString();
    }
}
