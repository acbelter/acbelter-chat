package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.message.ChatSendMessage;
import com.acbelter.chat.message.base.Chat;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.MessageStore;
import com.acbelter.chat.message.result.CommandResultMessage;
import com.acbelter.chat.net.SessionManager;
import com.acbelter.chat.session.Session;

import java.io.IOException;
import java.util.List;

public class ChatSendCommand extends Command {
    private MessageStore messageStore;
    private SessionManager sessionManager;

    public ChatSendCommand() {
        super();
        name = "chat_send";
        description = "<id> <message> Отправить сообщение в заданный чат, " +
                "чат должен быть в списке чатов пользователя (только для залогиненных пользователей)";
    }

    public ChatSendCommand(SessionManager sessionManager, MessageStore messageStore) {
        this();
        this.sessionManager = sessionManager;
        this.messageStore = messageStore;
    }

    @Override
    public Message execute(Session session, Message message) {
        if (session.getSessionUser() == null) {
            return new CommandResultMessage(CommandResultState.NOT_LOGGED, "You need to login.");
        }

        ChatSendMessage chatSendMessage = (ChatSendMessage) message;
        chatSendMessage.setSenderNick(session.getSessionUser().getNick());
        Chat chat = messageStore.getChatById(chatSendMessage.getChatId());
        if (chat == null) {
            return new CommandResultMessage(CommandResultState.FAILED, "Chat with id " +
                    chatSendMessage.getChatId() + " doesn't exist.");
        }
        List<Long> participants = chat.getParticipantIds();
        if (!participants.contains(message.getSender())) {
            return new CommandResultMessage(CommandResultState.FAILED, "You can't send message to this chat.");
        }
        try {
            messageStore.addMessage(chatSendMessage.getChatId(), chatSendMessage);
            for (Long partId : participants) {
                Session userSession = sessionManager.getSessionByUser(partId);
                if (userSession != null) {
                    userSession.getConnectionHandler().send(chatSendMessage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
