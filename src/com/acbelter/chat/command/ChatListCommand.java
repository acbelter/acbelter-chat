package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.message.ChatListMessage;
import com.acbelter.chat.message.base.Chat;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.MessageStore;
import com.acbelter.chat.message.result.ChatListResultMessage;
import com.acbelter.chat.message.result.CommandResultMessage;
import com.acbelter.chat.session.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListCommand extends Command {
    private MessageStore messageStore;

    public ChatListCommand() {
        super();
        name = "chat_list";
        description = "<> Получить список чатов пользователя (только для залогиненных пользователей)";
    }

    public ChatListCommand(MessageStore messageStore) {
        this();
        this.messageStore = messageStore;
    }

    @Override
    public Message execute(Session session, Message message) {
        if (session.getSessionUser() == null) {
            return new CommandResultMessage(CommandResultState.NOT_LOGGED, "Yoy need to login.");
        }

        ChatListMessage chatListMessage = (ChatListMessage) message;
        List<Long> chatIdList = messageStore.getChatsByUserId(chatListMessage.getSender());
        Map<Long, List<Long>> chatData = new HashMap<>(chatIdList.size());
        for (Long chatId : chatIdList) {
            Chat chat = messageStore.getChatById(chatId);
            chatData.put(chatId, chat.getParticipantIds());
        }

        return new ChatListResultMessage(chatData);
    }
}
