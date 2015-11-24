package com.acbelter.chat.net.netty;

import com.acbelter.chat.command.*;
import com.acbelter.chat.command.base.Command;
import com.acbelter.chat.command.base.CommandHandler;
import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.jdbc.MessageDatabaseStore;
import com.acbelter.chat.jdbc.UserDatabaseStore;
import com.acbelter.chat.message.base.MessageStore;
import com.acbelter.chat.message.base.UserStore;
import com.acbelter.chat.net.Protocol;
import com.acbelter.chat.net.ProtocolStub;
import com.acbelter.chat.net.SessionManager;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class NettyServer {
    public static final int PORT = 19000;
    static Logger log = LoggerFactory.getLogger(NettyServer.class);

    public NettyServer(Protocol protocol, SessionManager sessionManager, CommandHandler commandHandler) {
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        SimpleChannelUpstreamHandler serverHandler = new NettyServerHandler(sessionManager, commandHandler);
        bootstrap.setPipelineFactory(new MessagePipelineFactory(serverHandler, protocol));
        bootstrap.bind(new InetSocketAddress(PORT));

        log.info("Server started on port " + PORT);
    }

    public static void main(String[] args) throws Exception {
        Protocol protocol = new ProtocolStub();
        SessionManager sessionManager = new SessionManager();

        UserStore userStore = new UserDatabaseStore();
        MessageStore messageStore = new MessageDatabaseStore(userStore);

        Map<CommandType, Command> commands = new HashMap<>();
        commands.put(CommandType.CHAT_CREATE, new ChatCreateCommand(messageStore));
        commands.put(CommandType.CHAT_FIND, new ChatFindCommand(messageStore));
        commands.put(CommandType.CHAT_HISTORY, new ChatHistoryCommand(messageStore));
        commands.put(CommandType.CHAT_LIST, new ChatListCommand(messageStore));
        commands.put(CommandType.CHAT_SEND, new ChatSendCommand(sessionManager, messageStore));
        commands.put(CommandType.HELP, new HelpCommand(commands));
        commands.put(CommandType.LOGIN, new LoginCommand(userStore, sessionManager));
        commands.put(CommandType.USER_INFO, new UserInfoCommand(userStore));
        commands.put(CommandType.USER, new UserCommand(userStore));
        commands.put(CommandType.USER_PASS, new UserPassCommand(userStore));

        CommandHandler handler = new CommandHandler(commands);
        new NettyServer(protocol, sessionManager, handler);
    }
}
