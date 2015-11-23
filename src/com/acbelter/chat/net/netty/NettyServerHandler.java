package com.acbelter.chat.net.netty;

import com.acbelter.chat.command.base.CommandHandler;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.net.ChannelConnectionHandler;
import com.acbelter.chat.net.ConnectionHandler;
import com.acbelter.chat.net.SessionManager;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NettyServerHandler extends SimpleChannelUpstreamHandler {
    static Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    private Map<Integer, ConnectionHandler> handlers = new HashMap<>();
    private SessionManager sessionManager;
    private CommandHandler commandHandler;

    public NettyServerHandler(SessionManager sessionManager, CommandHandler commandHandler) {
        this.sessionManager = sessionManager;
        this.commandHandler = commandHandler;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            log.debug("Channel state: " + State.getState(ctx.getChannel().getInterestOps()));
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        log.info("Server open channel " + e.getChannel().getId());

        Channel channel = e.getChannel();
        ConnectionHandler handler = new ChannelConnectionHandler(sessionManager.createSession(), channel);
        handler.addListener(commandHandler);

        handlers.put(channel.getId(), handler);

//        Thread thread = new Thread(handler);
//        thread.start();

        super.channelOpen(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        log.info("Server receive message: " + e.getMessage().getClass());
        ConnectionHandler handler = handlers.get(e.getChannel().getId());
        handler.receive((Message) e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.error("Unexpected exception from downstream: " + e.toString());
        e.getChannel().close();
    }

    public void stopServer() {
        for (ConnectionHandler handler : handlers.values()) {
            handler.stop();
        }
    }
}
