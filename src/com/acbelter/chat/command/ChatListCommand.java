package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.session.Session;

public class ChatListCommand extends Command {
    public ChatListCommand() {
        super();
        name = "chat_list";
        description = "<> Получить список чатов пользователя (только для залогиненных пользователей)";
    }

    @Override
    public Message execute(Session session, Message message) {
        return null;
    }
}
