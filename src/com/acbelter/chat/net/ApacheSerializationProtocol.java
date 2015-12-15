package com.acbelter.chat.net;

import com.acbelter.chat.message.base.Message;
import org.apache.commons.lang.SerializationUtils;

public class ApacheSerializationProtocol implements Protocol {
    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
        return (Message) SerializationUtils.deserialize(bytes);
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        return SerializationUtils.serialize(msg);
    }
}
