package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.message.UserMessage;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.base.UserStore;
import com.acbelter.chat.message.result.CommandResultMessage;
import com.acbelter.chat.session.Session;

public class UserCommand extends Command {
    private UserStore userStore;

    public UserCommand() {
        super();
        name = "user";
        description = "<nick> Добавить никнейм для текущего пользователя (только для залогиненных пользователей).";
    }

    public UserCommand(UserStore userStore) {
        this();
        this.userStore = userStore;
    }

    @Override
    public Message execute(Session session, Message message) {
        if (session.getSessionUser() == null) {
            return new CommandResultMessage(CommandResultState.NOT_LOGGED, "You need to login.");
        }

        UserMessage userMessage = (UserMessage) message;
        User user = userStore.getUserById(userMessage.getSender());
        user.setNick(userMessage.getNick());
        session.setSessionUser(user);
        return new CommandResultMessage(CommandResultState.OK, "Now you nick is: " + userMessage.getNick());
    }
}
