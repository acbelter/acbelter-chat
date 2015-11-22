package com.acbelter.chat.net.netty;

import com.acbelter.chat.command.base.CommandParser;
import com.acbelter.chat.message.ChatSendMessage;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.result.*;
import com.acbelter.chat.net.*;
import com.acbelter.chat.session.Session;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class NettyClient implements MessageListener {
    public static final int PORT = 19000;
    public static final String HOST = "localhost";

    private ClientBootstrap bootstrap;
    private Channel channel;

    private ConnectionHandler handler;
    private Protocol protocol = new ProtocolStub();

    public NettyClient() {
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new MessagePipelineFactory(new NettyClientHandler()));

        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(HOST, PORT));
        channel = channelFuture.awaitUninterruptibly().getChannel();

        Session session = new Session();
        handler = new ChannelConnectionHandler(protocol, session, channel);
        handler.addListener(this);

        NettyClientHandler clientHandler = channel.getPipeline().get(NettyClientHandler.class);
        clientHandler.setConnectionHandler(handler);

        Thread thread = new Thread(handler);
        thread.start();

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

    private void processInput(String line) throws IOException {
        if (CommandParser.isCommand(line)) {
            String name = CommandParser.parseName(line);
            String[] args = CommandParser.parseArgs(line);

            Message commandMessage = MessageBuilder.buildMessage(name, args);
            if (commandMessage != null) {
                handler.send(commandMessage);
            } else {
                System.out.println("Bad command.");
            }
        } else {
            System.out.println("Invalid input: " + line);
        }
    }

    @Override
    public void onMessage(Session session, Message message) {
        switch (message.getType()) {
            case CHAT_CREATE_RESULT: {
                ChatCreateResultMessage chatCreateResultMessage = (ChatCreateResultMessage) message;
                System.out.println("New chat id: " + chatCreateResultMessage.getNewChatId());
                break;
            }
            case CHAT_FIND_RESULT: {
                ChatFindResultMessage chatFindResultMessage = (ChatFindResultMessage) message;
                chatFindResultMessage.getMessages().forEach(System.out::println);
                break;
            }
            case CHAT_HISTORY_RESULT: {
                ChatHistoryResultMessage chatHistoryResultMessage = (ChatHistoryResultMessage) message;
                chatHistoryResultMessage.getMessages().forEach(System.out::println);
                break;
            }
            case CHAT_LIST_RESULT: {
                ChatListResultMessage chatListResultMessage = (ChatListResultMessage) message;
                for (Map.Entry<Long, List<Long>> chatData : chatListResultMessage.getChatData().entrySet()) {
                    System.out.println(chatData.getKey() + ":" + Arrays.toString(chatData.getValue().toArray()));
                }
                break;
            }
            case HELP_RESULT: {
                HelpResultMessage helpResultMessage = (HelpResultMessage) message;
                helpResultMessage.getHelpContent().forEach(System.out::println);
                break;
            }
            case LOGIN_RESULT: {
                LoginResultMessage loginResultMessage = (LoginResultMessage) message;
                User user = new User(loginResultMessage.getLogin());
                user.setId(loginResultMessage.getUserId());
                session.setSessionUser(user);
                System.out.println("Success login. User id: " + user.getId());
                break;
            }
            case USER_INFO_RESULT: {
                UserInfoResultMessage userInfoResultMessage = (UserInfoResultMessage) message;
                User user = userInfoResultMessage.getUser();
                System.out.println("User id: " + user.getId());
                System.out.println("User login: " + user.getLogin());
                System.out.println("User nick: " + user.getNick());
                break;
            }
            case CHAT_SEND: {
                ChatSendMessage chatSendMessage = (ChatSendMessage) message;
                System.out.println(chatSendMessage.getSenderNick() + " (chat_id=" + chatSendMessage.getChatId() +
                        "): " + chatSendMessage.getMessage());
                break;
            }
            case COMMAND_RESULT: {
                CommandResultMessage commandResultMessage = (CommandResultMessage) message;
                System.out.println(commandResultMessage.getData());
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        NettyClient client = new NettyClient();

        Scanner scanner = new Scanner(System.in);
        System.out.println("$");
        while (true) {
            String input = scanner.nextLine();
            if ("q".equals(input)) {
                client.stop();
                return;
            }
            client.processInput(input);
        }
    }
}
