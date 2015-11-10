package com.acbelter.chat.net;

import com.acbelter.chat.message.base.Message;

import java.io.*;
import java.nio.ByteBuffer;

public class SerializationProtocol implements Protocol {
    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {

            out.writeObject(msg);
            byte[] objData = bos.toByteArray();
            int size = objData.length;

            ByteBuffer buf = ByteBuffer.allocate(size + 4);
            buf.putInt(size);
            buf.put(objData);

            return buf.array();

        } catch (IOException e) {
            throw new ProtocolException("Failed to encode message", e);
        }
    }

    @Override
    public Message decode(byte[] data) throws ProtocolException {
        ByteBuffer buf = ByteBuffer.wrap(data);
        int size = buf.getInt();
        if (size != data.length - 4) {
            throw new ProtocolException(String.format("Invalid data. Expected size: %d, actual size: %d", size, data.length));
        }
        byte[] objData = new byte[size];
        buf.get(objData);

        try (ByteArrayInputStream bis = new ByteArrayInputStream(objData);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (Message) in.readObject();
        } catch (IOException e) {
            throw new ProtocolException("Failed to decode message", e);
        } catch (ClassNotFoundException e) {
            throw new ProtocolException("No class found",  e);
        }
    }
}
