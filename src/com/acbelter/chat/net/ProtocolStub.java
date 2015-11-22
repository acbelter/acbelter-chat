package com.acbelter.chat.net;

import com.acbelter.chat.message.base.Message;

public class ProtocolStub implements Protocol {
    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        return null;
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        return new byte[0];
    }
}
