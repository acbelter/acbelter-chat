package com.acbelter.chat.message.result;

import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.base.Message;

public class CommandResultMessage extends Message {
    private CommandResultState state;
    private String data;

    public CommandResultMessage() {
        setType(CommandType.RESULT);
    }

    public CommandResultMessage(CommandResultState state, String data) {
        this();
        this.state = state;
        this.data = data;
    }

    public CommandResultState getState() {
        return state;
    }

    public void setState(CommandResultState state) {
        this.state = state;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommandResultMessage{" +
                "state=" + state +
                ", data='" + data + '\'' +
                "} " + super.toString();
    }
}
