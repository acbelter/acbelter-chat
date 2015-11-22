package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.message.ChatHistoryMessage;
import com.acbelter.chat.message.ChatSendMessage;
import com.acbelter.chat.message.base.Chat;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.MessageStore;
import com.acbelter.chat.message.result.ChatHistoryResultMessage;
import com.acbelter.chat.message.result.CommandResultMessage;
import com.acbelter.chat.session.Session;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryCommand extends Command {
    private MessageStore messageStore;

    public ChatHistoryCommand() {
        super();
        name = "chat_history";
        description = "<chat_id> Список сообщений из указанного чата (только для залогиненных пользователей)";
    }

    public ChatHistoryCommand(MessageStore messageStore) {
        this();
        this.messageStore = messageStore;
    }

    private static List<String> getRecentMessages(List<String> data, int count) {
        if (count < 0 || data.size() <= count) {
            return data;
        } else {
            return data.subList(data.size() - count, data.size());
        }
    }

    @Override
    public Message execute(Session session, Message message) {
        if (session.getSessionUser() == null) {
            return new CommandResultMessage(CommandResultState.NOT_LOGGED, "You need to login.");
        }

        ChatHistoryMessage chatHistoryMessage = (ChatHistoryMessage) message;
        Chat chat = messageStore.getChatById(chatHistoryMessage.getChatId());
        if (chat != null) {
            List<String> messages = new ArrayList<>();
            for (Long messageId : chat.getMessageIds()) {
                ChatSendMessage chatMessage = messageStore.getMessageById(messageId);
                messages.add(chatMessage.getFormatted());
            }
            messages = getRecentMessages(messages, 50);
            return new ChatHistoryResultMessage(messages);
        } else {
            return new CommandResultMessage(CommandResultState.FAILED, "Chat isn't exists.");
        }
    }
}
