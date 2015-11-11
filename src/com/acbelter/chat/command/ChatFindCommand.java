package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.message.ChatFindMessage;
import com.acbelter.chat.message.ChatSendMessage;
import com.acbelter.chat.message.base.Chat;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.MessageStore;
import com.acbelter.chat.message.result.ChatFindResultMessage;
import com.acbelter.chat.message.result.CommandResultMessage;
import com.acbelter.chat.session.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ChatFindCommand extends Command {
    private MessageStore messageStore;

    public ChatFindCommand() {
        super();
        name = "chat_find";
        description = "<chat_id> <regex> Поиск в чате подстроки, " +
                "соответсвующей регулярному выражению (только для залогиненных пользователей)";
    }

    public ChatFindCommand(MessageStore messageStore) {
        this();
        this.messageStore = messageStore;
    }

    private static List<String> findMessagesByRegex(List<String> data, String regex) throws PatternSyntaxException {
        return data
                .stream()
                .filter(Pattern.compile(regex).asPredicate())
                .collect(Collectors.toList());
    }


    @Override
    public Message execute(Session session, Message message) {
        if (session.getSessionUser() == null) {
            return new CommandResultMessage(CommandResultState.NOT_LOGGED, "Yoy need to login.");
        }

        ChatFindMessage chatFindMessage = (ChatFindMessage) message;
        Chat chat = messageStore.getChatById(chatFindMessage.getChatId());
        if (chat != null) {
            List<String> messages = new ArrayList<>();
            for (Long messageId : chat.getMessageIds()) {
                ChatSendMessage chatMessage = messageStore.getMessageById(messageId);
                messages.add(chatMessage.getMessage());
            }
            try {
                messages = findMessagesByRegex(messages, chatFindMessage.getRegex());
            } catch (PatternSyntaxException e) {
                messages.clear();
            }
            return new ChatFindResultMessage(messages);
        } else {
            return new CommandResultMessage(CommandResultState.FAILED, "Chat isn't exists.");
        }
    }
}
