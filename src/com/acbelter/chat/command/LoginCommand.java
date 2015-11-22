package com.acbelter.chat.command;

import com.acbelter.chat.HashUtil;
import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.message.LoginMessage;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.base.UserStore;
import com.acbelter.chat.message.result.CommandResultMessage;
import com.acbelter.chat.message.result.LoginResultMessage;
import com.acbelter.chat.net.SessionManager;
import com.acbelter.chat.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginCommand extends Command {
    static Logger log = LoggerFactory.getLogger(LoginCommand.class);

    private UserStore userStore;
    private SessionManager sessionManager;

    public LoginCommand() {
        super();
        name = "login";
        description = "<login> <password> или <login> <password> <repeat_password> Залогиниться или зарегестрироваться.";
    }

    public LoginCommand(UserStore userStore, SessionManager sessionManager) {
        this();
        this.userStore = userStore;
        this.sessionManager = sessionManager;
    }

    @Override
    public Message execute(Session session, Message msg) {
        LoginMessage loginMsg = (LoginMessage) msg;
        User user = userStore.getUser(loginMsg.getLogin());
        if (loginMsg.isLoginMessage()) {
            // Заходим под существующим пользователем
            if (user == null) {
                String data = "User " + loginMsg.getLogin() + " doesn't exist.";
                return new CommandResultMessage(CommandResultState.FAILED, data);
            }

            if (validatePassword(user, loginMsg.getPassword())) {
                session.setSessionUser(user);
                sessionManager.registerUser(user.getId(), session.getId());
                log.info("Success login: {}", user);
                return new LoginResultMessage(user.getLogin(), user.getId());
            } else {
                log.info("Failed login: {}", user);
                return new CommandResultMessage(CommandResultState.FAILED,
                        "Invalid login or password.");
            }
        } else {
            if (!loginMsg.getPassword().equals(loginMsg.getRepeatPassword())) {
                return new CommandResultMessage(CommandResultState.FAILED,
                        "Two passwords are not equal.");
            }
            // Регистрируем нового пользователя или пытаемся зайти, если такой пользователь существует
            if (user == null) {
                User newUser = new User(loginMsg.getLogin(), HashUtil.generateHash(loginMsg.getPassword()));
                newUser = userStore.addUser(newUser);
                session.setSessionUser(newUser);
                sessionManager.registerUser(newUser.getId(), session.getId());
                log.info("Success registration: {}", newUser);
                return new LoginResultMessage(newUser.getLogin(), newUser.getId());
            } else {
                if (validatePassword(user, loginMsg.getPassword())) {
                    session.setSessionUser(user);
                    sessionManager.registerUser(user.getId(), session.getId());
                    log.info("Success login: {}", user);
                    return new LoginResultMessage(user.getLogin(), user.getId());
                } else {
                    log.info("Failed login: {}", user);
                    return new CommandResultMessage(CommandResultState.FAILED,
                            "Invalid login or password.");
                }
            }
        }
    }

    private static boolean validatePassword(User user, String password) {
        if (user == null || user.getPasswordHash() == null || password == null) {
            return false;
        }
        return user.getPasswordHash().equalsIgnoreCase(HashUtil.generateHash(password));
    }
}
