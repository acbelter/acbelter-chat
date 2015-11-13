package com.acbelter.chat.net.netty;

import com.acbelter.chat.message.ChatSendMessage;
import com.acbelter.chat.message.base.Message;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Client {
    private ClientBootstrap bootstrap;
    private Channel channel;
    private NettyClientHandler handler;

    public Client(String host, int port) {
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new MessagePipelineFactory(new NettyClientHandler()));

        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(host, port));

        channel = channelFuture.awaitUninterruptibly().getChannel();
        handler = channel.getPipeline().get(NettyClientHandler.class);
    }

    public void stop() {
        // Close the connection.
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }

        // Shut down all thread pools to exit.
        if (bootstrap != null) {
            bootstrap.releaseExternalResources();
        }
    }

    private ChatSendMessage buildMessage() {
        long id = 1;
        long senderId = 2;
        long chatId = 3;
        String content = "TEST MESSAGE";
        long timestamp = System.currentTimeMillis();

        ChatSendMessage msg = new ChatSendMessage(chatId, content);
        msg.setId(id);
        msg.setSender(senderId);

        return msg;
    }

    public void sendTestMessage() {
        Message msg = buildMessage();
        send(msg);
    }

    public void send(Message message) {
        handler.send(message);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8080);
        client.sendTestMessage();
    }
}
