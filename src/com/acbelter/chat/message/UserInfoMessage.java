package com.acbelter.chat.message;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

/**
 * /user_info <user_id> or /user_info
 */
public class UserInfoMessage extends Message {
    private Long userId;

    public UserInfoMessage() {
        setType(CommandType.USER_INFO);
    }

    public UserInfoMessage(Long userId) {
        this();
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserInfoMessage{" +
                "userId=" + userId +
                "} " + super.toString();
    }
}
