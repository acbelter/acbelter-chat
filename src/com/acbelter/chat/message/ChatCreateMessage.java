package com.acbelter.chat.message;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * /chat_create <user_id list>
 */
public class ChatCreateMessage extends Message {
    private Set<Long> userIdSet = new HashSet<>();

    public ChatCreateMessage() {
        setType(CommandType.CHAT_CREATE);
    }

    public ChatCreateMessage(List<Long> userIdList) {
        this();
        this.userIdSet.addAll(userIdList);
    }

    public ChatCreateMessage(Long userId) {
        this();
        userIdSet.add(userId);
    }

    public List<Long> getUserIds() {
        return new ArrayList<>(userIdSet);
    }

    public void addUserId(Long id) {
        userIdSet.add(id);
    }

    public void removeUserId(Long id) {
        userIdSet.remove(id);
    }

    @Override
    public String toString() {
        return "ChatCreateMessage{" +
                "userIdSet=" + userIdSet +
                "} " + super.toString();
    }
}
