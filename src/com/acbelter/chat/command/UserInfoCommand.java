package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.message.UserInfoMessage;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.base.UserStore;
import com.acbelter.chat.message.result.CommandResultMessage;
import com.acbelter.chat.message.result.UserInfoResultMessage;
import com.acbelter.chat.session.Session;

public class UserInfoCommand extends Command {
    private UserStore userStore;

    public UserInfoCommand() {
        super();
        name = "user_info";
        description = "<id> Получить всю информацию о пользователе, " +
                "без аргументов - о себе (только для залогиненных пользователей).";
    }

    public UserInfoCommand(UserStore userStore) {
        this();
        this.userStore = userStore;
    }

    @Override
    public Message execute(Session session, Message message) {
        if (session.getSessionUser() == null) {
            return new CommandResultMessage(CommandResultState.NOT_LOGGED, "You need to login.");
        }

        UserInfoMessage userInfoMessage = (UserInfoMessage) message;
        User user;
        if (userInfoMessage.getUserId() != null) {
            user = userStore.getUserById(userInfoMessage.getUserId());
        } else {
            user = userStore.getUserById(userInfoMessage.getSender());
        }
        return new UserInfoResultMessage(user);
    }
}
