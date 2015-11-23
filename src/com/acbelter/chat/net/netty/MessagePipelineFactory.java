package com.acbelter.chat.net.netty;

import com.acbelter.chat.net.Protocol;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class MessagePipelineFactory implements ChannelPipelineFactory {
    private ChannelUpstreamHandler upstreamHandler;
    // TODO Use Protocol
    private Protocol protocol;

    public MessagePipelineFactory(ChannelUpstreamHandler upstreamHandler, Protocol protocol) {
        this.upstreamHandler = upstreamHandler;
        this.protocol = protocol;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        return Channels.pipeline(
                new ObjectEncoder(),
                new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())),
                upstreamHandler);
    }
}
