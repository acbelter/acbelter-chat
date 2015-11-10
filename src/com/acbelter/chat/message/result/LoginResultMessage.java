package com.acbelter.chat.message.result;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

public class LoginResultMessage extends Message {
    private String login;
    private Long userId;

    public LoginResultMessage() {
        setType(CommandType.LOGIN_RESULT);
    }

    public LoginResultMessage(String login, Long userId) {
        this();
        this.login = login;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "LoginResultMessage{" +
                "login='" + login + '\'' +
                ", userId=" + userId +
                "} " + super.toString();
    }
}
