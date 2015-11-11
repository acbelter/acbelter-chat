package com.acbelter.chat.command;

import com.acbelter.chat.HashUtil;
import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.message.UserPassMessage;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.base.UserStore;
import com.acbelter.chat.message.result.CommandResultMessage;
import com.acbelter.chat.session.Session;

public class UserPassCommand extends Command {
    private UserStore userStore;

    public UserPassCommand() {
        super();
        name = "user_pass";
        description = "<old_pass> <new_pass> Сменить пароль (только для залогиненных пользователей).";
    }

    public UserPassCommand(UserStore userStore) {
        this();
        this.userStore = userStore;
    }

    @Override
    public Message execute(Session session, Message message) {
        if (session.getSessionUser() == null) {
            return new CommandResultMessage(CommandResultState.NOT_LOGGED, "You need to login.");
        }

        UserPassMessage userPassMessage = (UserPassMessage) message;
        User user = userStore.getUserById(userPassMessage.getSender());
        String oldPasswordHash = HashUtil.generateHash(userPassMessage.getOldPassword());
        if (oldPasswordHash != null && user.getPasswordHash().equalsIgnoreCase(oldPasswordHash)) {
            String newPasswordHash = HashUtil.generateHash(userPassMessage.getNewPassword());
            if (newPasswordHash == null) {
                return new CommandResultMessage(CommandResultState.FAILED, "Unable to change password.");
            } else {
                user.setPasswordHash(newPasswordHash);
                userStore.updateUser(user);
                session.setSessionUser(user);
                return new CommandResultMessage(CommandResultState.OK, "Password is changed.");
            }
        } else {
            return new CommandResultMessage(CommandResultState.FAILED, "Invalid password.");
        }
    }
}
