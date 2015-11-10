package com.acbelter.chat.message.result;

import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

public class HelpResultMessage extends Message {
    private String content;

    public HelpResultMessage() {
        setType(CommandType.HELP_RESULT);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "HelpResultMessage{" +
                "content='" + content + '\'' +
                "} " + super.toString();
    }
}
