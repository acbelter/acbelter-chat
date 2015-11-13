package com.acbelter.chat.net.netty;

import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.net.ConnectionHandler;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelUpstreamHandler {
    static Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

    private ConnectionHandler handler;

    public ConnectionHandler getConnectionHandler() {
        return handler;
    }

    public void setConnectionHandler(ConnectionHandler handler) {
        this.handler = handler;
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
        log.info("Client open channel " + e.getChannel().getId());
        super.channelOpen(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        log.info("Message received: " + e.getMessage().getClass().getName());
        handler.receive((Message) e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.error("Unexpected exception from downstream: " + e.toString());
        e.getChannel().close();
    }
}
