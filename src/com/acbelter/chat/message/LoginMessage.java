package com.acbelter.chat.message;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

/**
 * /login <login> <password> or /login <login> <password> <repeat_password>
 */
public class LoginMessage extends Message {
    private String login;
    private String password;
    private String repeatPassword;

    public LoginMessage() {
        setType(CommandType.LOGIN);
    }

    public LoginMessage(String login, String password) {
        this();
        this.login = login;
        this.password = password;
    }

    public LoginMessage(String login, String password, String repeatPassword) {
        this(login, password);
        this.repeatPassword = repeatPassword;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public boolean isLoginMessage() {
        return repeatPassword == null;
    }

    public boolean isRegisterMessage() {
        return repeatPassword != null;
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", repeatPassword='" + repeatPassword + '\'' +
                "} " + super.toString();
    }
}
