package com.acbelter.chat.message;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

/**
 * /user_pass <old_password> <new_password>
 */
public class UserPassMessage extends Message {
    private String oldPassword;
    private String newPassword;

    public UserPassMessage() {
        setType(CommandType.USER_PASS);
    }

    public UserPassMessage(String oldPassword, String newPassword) {
        this();
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "UserPassMessage{" +
                "oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                "} " + super.toString();
    }
}
