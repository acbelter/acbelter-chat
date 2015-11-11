package com.acbelter.chat.net;

import com.acbelter.chat.command.base.CommandParser;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.result.HelpResultMessage;
import com.acbelter.chat.message.result.LoginResultMessage;
import com.acbelter.chat.session.Session;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ThreadedClient implements MessageListener {
    public static final int PORT = 19000;
    public static final String HOST = "localhost";

    ConnectionHandler handler;

    private Protocol protocol = new SerializationProtocol();

    public ThreadedClient() {
        init();
    }

    private void init() {
        try {
            Socket socket = new Socket(HOST, PORT);
            Session session = new Session();
            handler = new SocketConnectionHandler(protocol, session, socket);

            // Этот класс будет получать уведомления от socket handler
            handler.addListener(this);

            Thread socketHandler = new Thread(handler);
            socketHandler.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
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
    public void onMessage(Session session, Message msg) {
        System.out.println("SESSION " + session.getSessionUser() + " MESSAGE " + msg);
        switch (msg.getType()) {
            case LOGIN_RESULT: {
                LoginResultMessage loginResultMessage = (LoginResultMessage) msg;
                User user = new User(loginResultMessage.getLogin());
                user.setId(loginResultMessage.getUserId());
                session.setSessionUser(user);
                System.out.println("Success login. User id: " + user.getId());
                break;
            }
            case HELP_RESULT: {
                HelpResultMessage helpResultMessage = (HelpResultMessage) msg;
                System.out.println(helpResultMessage.getContent());
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ThreadedClient client = new ThreadedClient();

        Scanner scanner = new Scanner(System.in);
        System.out.println("$");
        while (true) {
            String input = scanner.nextLine();
            if ("q".equals(input)) {
                return;
            }
            client.processInput(input);
        }
    }
}
