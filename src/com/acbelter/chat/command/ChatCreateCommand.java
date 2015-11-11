package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.message.ChatCreateMessage;
import com.acbelter.chat.message.base.Chat;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.MessageStore;
import com.acbelter.chat.message.result.ChatCreateResultMessage;
import com.acbelter.chat.message.result.CommandResultMessage;
import com.acbelter.chat.session.Session;

import java.util.HashSet;
import java.util.Set;

public class ChatCreateCommand extends Command {
    private MessageStore messageStore;

    public ChatCreateCommand() {
        super();
        name = "chat_create";
        description = "<user_id list> Создать новый чат, " +
                "список пользователей приглашенных в чат (только для залогиненных пользователей).";
    }

    public ChatCreateCommand(MessageStore messageStore) {
        this();
        this.messageStore = messageStore;
    }

    @Override
    public Message execute(Session session, Message message) {
        if (session.getSessionUser() == null) {
            return new CommandResultMessage(CommandResultState.NOT_LOGGED, "Yoy need to login.");
        }

        ChatCreateMessage chatCreateMessage = (ChatCreateMessage) message;
        Set<Long> userIds = new HashSet<>(chatCreateMessage.getUserIds());
        if (!userIds.contains(session.getSessionUser().getId())) {
            userIds.add(session.getSessionUser().getId());
        }
        Chat newChat = messageStore.createChat(userIds);
        return new ChatCreateResultMessage(newChat.getId());
    }
}
