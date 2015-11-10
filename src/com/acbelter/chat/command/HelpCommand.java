package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.result.HelpResultMessage;
import com.acbelter.chat.session.Session;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class HelpCommand extends Command {
    private Map<CommandType, Command> commands;

    public HelpCommand() {
        super();
        name = "help";
        description = "<> Показать список команд и общий хэлп по месседжеру.";
    }

    public HelpCommand(Map<CommandType, Command> commands) {
        this();
        this.commands = new TreeMap<>();
        this.commands.putAll(commands);
    }

    @Override
    public Message execute(Session session, Message msg) {
        HelpResultMessage helpResultMessage = new HelpResultMessage();
        StringBuilder builder = new StringBuilder();
        for (Command cmd : commands.values()) {
            builder.append(cmd.getName()).append("\t").append(cmd.getDescription()).append("\n");
        }
        helpResultMessage.setContent(builder.toString());
        try {
            session.getConnectionHandler().send(helpResultMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
