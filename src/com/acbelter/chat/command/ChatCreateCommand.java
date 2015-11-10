package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.session.Session;

public class ChatCreateCommand extends Command {
    public ChatCreateCommand() {
        super();
        name = "chat_create";
        description = "<user_id list> Создать новый чат, " +
                "список пользователей приглашенных в чат (только для залогиненных пользователей)";
    }

    @Override
    public Message execute(Session session, Message message) {
        return null;
    }
}
