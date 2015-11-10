package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.session.Session;

public class UserInfoCommand extends Command {
    public UserInfoCommand() {
        super();
        name = "user_info";
        description = "<id> Получить всю информацию о пользователе, " +
                "без аргументов - о себе (только для залогиненных пользователей).";
    }

    @Override
    public Message execute(Session session, Message message) {
        return null;
    }
}
