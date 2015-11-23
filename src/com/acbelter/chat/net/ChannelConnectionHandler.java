package com.acbelter.chat.net;

import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.session.Session;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChannelConnectionHandler implements ConnectionHandler {
    static Logger log = LoggerFactory.getLogger(ChannelConnectionHandler.class);

//    private BlockingQueue<Message> inQueue = new LinkedBlockingQueue<>();
    private List<MessageListener> listeners = new ArrayList<>();
    private Channel channel;
    private Session session;

    public ChannelConnectionHandler(Session session, Channel channel) {
        this.session = session;
        this.channel = channel;
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

        log.info("Send: {}", msg);

        channel.write(msg);
    }

    @Override
    public void receive(Message msg) {
        //inQueue.offer(msg);

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
    public void run() {
//        while (!Thread.currentThread().isInterrupted()) {
//            try {
//                Message msg = inQueue.take();
//                log.info("Received: {}", msg);
//                notifyListeners(session, msg);
//            } catch (Exception e) {
//                log.error("Failed to handle connection: {}", e);
//                e.printStackTrace();
//                Thread.currentThread().interrupt();
//            }
//        }
//        channel.close();
    }

    @Override
    public void stop() {
//        Thread.currentThread().interrupt();
        channel.close();
    }
}
