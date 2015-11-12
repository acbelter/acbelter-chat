package com.acbelter.chat.command;

import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.result.HelpResultMessage;
import com.acbelter.chat.session.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        this.commands = commands;
    }

    @Override
    public Message execute(Session session, Message msg) {
        HelpResultMessage helpResultMessage = new HelpResultMessage();
        List<String> helpContent = new ArrayList<>(commands.size());
        for (Command cmd : new TreeMap<>(commands).values()) {
            helpContent.add(cmd.getName() + "\t" + cmd.getDescription());
        }
        helpResultMessage.setHelpContent(helpContent);
        try {
            session.getConnectionHandler().send(helpResultMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
