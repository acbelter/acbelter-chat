package com.acbelter.chat.net.netty;

import com.acbelter.chat.message.base.Message;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelUpstreamHandler {
    static Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

    private volatile Channel channel;

    public void send(Message message) {
        channel.write(message);
        log.info(String.format("Message %d was sent", message.getId()));
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
        channel = e.getChannel();
        super.channelOpen(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, final MessageEvent e) {
        log.info("Message received: " + e.getMessage().getClass().getName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.error("Unexpected exception from downstream: " + e.toString());
        e.getChannel().close();
    }
}
