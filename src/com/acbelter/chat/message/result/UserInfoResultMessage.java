package com.acbelter.chat.message.result;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.User;

public class UserInfoResultMessage extends Message {
    private User user;

    public UserInfoResultMessage() {
        setType(CommandType.USER_INFO_RESULT);
    }

    public UserInfoResultMessage(User user) {
        this();
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserInfoResultMessage{" +
                "user=" + user +
                "} " + super.toString();
    }
}
