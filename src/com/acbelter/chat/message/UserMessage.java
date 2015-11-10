package com.acbelter.chat.message;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

/**
 * /user <nick>
 */
public class UserMessage extends Message {
    private String nick;

    public UserMessage() {
        setType(CommandType.USER);
    }

    public UserMessage(String nick) {
        this();
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "nick='" + nick + '\'' +
                "} " + super.toString();
    }
}
