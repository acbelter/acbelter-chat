package com.acbelter.chat.command.base;

public abstract class CommandResult {
    enum Status {
        OK,
        FAILED,
        NOT_LOGGED,
    }

    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
