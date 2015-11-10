package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.session.Session;

public class ChatFindCommand extends Command {
    public ChatFindCommand() {
        super();
        name = "chat_find";
        description = "<chat_id> <regex> Поиск в чате подстроки, " +
                "соответсвующей регулярному выражению (только для залогиненных пользователей)";
    }

    @Override
    public Message execute(Session session, Message message) {
        return null;
    }
}
