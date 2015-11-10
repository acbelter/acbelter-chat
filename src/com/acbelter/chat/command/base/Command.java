package com.acbelter.chat.command.base;

import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.session.Session;

public abstract class Command {
    protected String name;
    protected String description;

    public abstract Message execute(Session session, Message message);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " " + description;
    }
}