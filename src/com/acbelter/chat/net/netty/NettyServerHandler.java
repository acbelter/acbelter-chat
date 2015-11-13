package com.acbelter.chat.net.netty;

import com.acbelter.chat.message.base.Message;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NettyServerHandler extends SimpleChannelUpstreamHandler {
    static Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    private Channel channel;
    private BlockingQueue<Message> inQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Message> outQueue = new LinkedBlockingQueue<>();

    public NettyServerHandler() {
        Thread receiveThread = new Thread(() -> {
            Message msg;
            while (true) {
                try {
                    msg = inQueue.take();
                    outQueue.offer(msg);
                } catch (InterruptedException e) {
                    log.error("Receive thread was interrupted");
                }
            }
        });

        Thread sendThread = new Thread(() -> {
            Message msg;
            while (true) {
                try {
                    msg = outQueue.take();
                    channel.write(msg);
                } catch (InterruptedException e) {
                    log.error("Send thread was interrupted");
                }
            }
        });

        receiveThread.start();
        sendThread.start();
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
        log.info("CHANNEL OPEN " + e.getChannel().getId());
        channel = e.getChannel();
        super.channelOpen(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        log.info("Server receive message: " + e.getMessage().getClass());
        inQueue.offer((Message) e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        log.error("Unexpected exception from downstream: " + e.toString());
        e.getChannel().close();
    }
}
