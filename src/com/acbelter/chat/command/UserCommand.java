package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.session.Session;

public class UserCommand extends Command {
    public UserCommand() {
        super();
        name = "user";
        description = "<nick> Добавить никнейм для текущего пользователя (только для залогиненных пользователей).";
    }

    @Override
    public Message execute(Session session, Message message) {
        return null;
    }
}
