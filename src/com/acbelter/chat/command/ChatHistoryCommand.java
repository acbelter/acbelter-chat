package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.session.Session;

public class ChatHistoryCommand extends Command {
    public ChatHistoryCommand() {
        super();
        name = "chat_history";
        description = "<chat_id> Список сообщений из указанного чата (только для залогиненных пользователей)";
    }

    @Override
    public Message execute(Session session, Message message) {
        return null;
    }
}
