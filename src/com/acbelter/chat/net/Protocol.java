package com.acbelter.chat.net;

import com.acbelter.chat.message.base.Message;

public interface Protocol {
    Message decode(byte[] bytes) throws ProtocolException;
    byte[] encode(Message msg) throws ProtocolException;
}
