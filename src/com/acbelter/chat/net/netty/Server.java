package com.acbelter.chat.net.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class.getName());

    public Server(int port) {
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new MessagePipelineFactory(new NettyServerHandler()));
        bootstrap.bind(new InetSocketAddress(port));

        LOGGER.info("Server started on port " + port);
    }

    public static void main(String[] args) throws Exception {
        new Server(8080);
    }
}
