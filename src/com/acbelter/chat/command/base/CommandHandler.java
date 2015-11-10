package com.acbelter.chat.command.base;

import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.net.MessageListener;
import com.acbelter.chat.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class CommandHandler implements MessageListener {
    static Logger log = LoggerFactory.getLogger(CommandHandler.class);

    private Map<CommandType, Command> commands;

    public CommandHandler(Map<CommandType, Command> commands) {
        this.commands = commands;
    }

    @Override
    public void onMessage(Session session, Message message) {
        Command cmd = commands.get(message.getType());
        log.info("onMessage: {} type {}", message, message.getType());
        Message result = cmd.execute(session, message);
        try {
            session.getConnectionHandler().send(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
