package com.acbelter.chat.message.base;

import com.acbelter.chat.command.base.CommandType;

import java.io.Serializable;

public abstract class Message implements Serializable {
    private Long id;
    private Long sender;

    private CommandType type;

    public Message() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "(sender=" + sender +
                ", type=" + type +
                ')';
    }
}
