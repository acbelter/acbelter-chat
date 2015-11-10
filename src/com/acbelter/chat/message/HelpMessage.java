package com.acbelter.chat.message;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

/**
 * /help
 */
public class HelpMessage extends Message {
    public HelpMessage() {
        setType(CommandType.HELP);
    }

    @Override
    public String toString() {
        return "HelpMessage{} " + super.toString();
    }
}
