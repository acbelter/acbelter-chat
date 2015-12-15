package com.acbelter.chat.net.nio;

import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.net.ConnectionHandler;
import com.acbelter.chat.net.MessageListener;
import com.acbelter.chat.net.Protocol;
import com.acbelter.chat.net.ProtocolException;
import com.acbelter.chat.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NioClientHandler implements ConnectionHandler {
    static Logger log = LoggerFactory.getLogger(NioClientHandler.class);
    private NioClient client;
    private Session session;
    private Protocol protocol;
    private List<MessageListener> listeners = new ArrayList<>();

    public NioClientHandler(NioClient client, Session session, Protocol protocol) {
        this.client = client;
        this.session = session;
        this.protocol = protocol;
        session.setConnectionHandler(this);
    }

    @Override
    public void send(Message msg) throws IOException {
        if (msg == null) {
            return;
        }

        if (session.getSessionUser() != null) {
            msg.setSender(session.getSessionUser().getId());
        }

        try {
            client.send(protocol.encode(msg));
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        log.info("Send: {}", msg);
    }

    @Override
    public void receive(Message msg) {
        log.info("Received: {}", msg);
        notifyListeners(session, msg);
    }

    @Override
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(Session session, Message msg) {
        listeners.forEach(it -> it.onMessage(session, msg));
    }


    @Override
    public void stop() {

    }

    @Override
    public void run() {

    }
}
