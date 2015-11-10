package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.session.Session;

public class UserPassCommand extends Command {
    public UserPassCommand() {
        super();
        name = "user_pass";
        description = "<old_pass> <new_pass> Сменить пароль (только для залогиненных пользователей).";
    }

    @Override
    public Message execute(Session session, Message message) {
        return null;
    }
}
