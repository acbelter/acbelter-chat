package com.acbelter.chat.net.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class MessagePipelineFactory implements ChannelPipelineFactory {
    private ChannelUpstreamHandler upstreamHandler;

    public MessagePipelineFactory(ChannelUpstreamHandler upstreamHandler) {
        this.upstreamHandler = upstreamHandler;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())),
                upstreamHandler);
    }
}
