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
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NioServerHandler implements ConnectionHandler {
    static Logger log = LoggerFactory.getLogger(NioServerHandler.class);
    private BlockingQueue<Message> inQueue = new LinkedBlockingQueue<>();
    private NioServer server;
    private SocketChannel socketChannel;
    private Session session;
    private Protocol protocol;

    private List<MessageListener> listeners = new ArrayList<>();

    public NioServerHandler(NioServer server, SocketChannel socketChannel, Session session, Protocol protocol) {
        this.server = server;
        this.socketChannel = socketChannel;
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
            server.send(socketChannel, protocol.encode(msg));
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive(Message msg) {
        log.info("Received: {}", msg);
        inQueue.offer(msg);
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
        inQueue.offer(new StopMessage());
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message msg = inQueue.take();
                if (msg.getClass() == StopMessage.class) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                log.info("Received: {}", msg);
                notifyListeners(session, msg);
            } catch (Exception e) {
//                log.error("Failed to handle connection: {}", e);
//                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        log.info("server handler stopped");
        server.removeHandler(socketChannel);

        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class StopMessage extends Message {

    }
}
