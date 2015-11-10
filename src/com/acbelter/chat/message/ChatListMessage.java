package com.acbelter.chat.message;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

/**
 * /chat_list
 */
public class ChatListMessage extends Message {
    public ChatListMessage() {
        setType(CommandType.CHAT_LIST);
    }

    @Override
    public String toString() {
        return "ChatListMessage{} " + super.toString();
    }
}
