package com.acbelter.chat;

import com.acbelter.chat.command.base.CommandResultState;
import com.acbelter.chat.command.base.CommandType;
import com.acbelter.chat.message.*;
import com.acbelter.chat.message.base.Message;
import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.result.*;
import com.acbelter.chat.net.Protocol;
import com.acbelter.chat.net.ProtocolException;
import org.apache.commons.lang.SerializationUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SerializationTest {
    private static final Map<CommandType, Message> MESSAGES = new HashMap<>();
    private Protocol protocol = new Protocol() {
        @Override
        public Message decode(byte[] bytes) throws ProtocolException {
            return (Message) SerializationUtils.deserialize(bytes);
        }

        @Override
        public byte[] encode(Message msg) throws ProtocolException {
            return SerializationUtils.serialize(msg);
        }
    };

    private static void setAbstractData(Message msg, Long id, Long sender) {
        msg.setId(id);
        msg.setSender(sender);
    }

    private static void assertEqualsAbstractData(Message origin, Message copy) {
        assertEquals(origin.getId(), copy.getId());
        assertEquals(origin.getSender(), copy.getSender());
        assertEquals(origin.getType(), copy.getType());
    }

    private static <T> void assertEqualsList(List<T> first, List<T> second) {
        assertEquals(first.size(), second.size());
        for (int i = 0; i < first.size(); i++) {
            assertEquals(first.get(i), second.get(i));
        }
    }

    @Before
    public void setup() {
        ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
        setAbstractData(chatCreateMessage, 1212L, 2112L);
        chatCreateMessage.addUserId(123L);
        chatCreateMessage.addUserId(232L);
        chatCreateMessage.addUserId(133123L);

        ChatFindMessage chatFindMessage = new ChatFindMessage();
        setAbstractData(chatFindMessage, 1123L, 2123L);
        chatFindMessage.setChatId(14523L);
        chatFindMessage.setRegex(".*");

        ChatHistoryMessage chatHistoryMessage = new ChatHistoryMessage();
        setAbstractData(chatHistoryMessage, 145L, 2456L);
        chatHistoryMessage.setChatId(123L);

        ChatListMessage chatListMessage = new ChatListMessage();
        setAbstractData(chatListMessage, 17343L, 25345L);

        ChatSendMessage chatSendMessage = new ChatSendMessage();
        setAbstractData(chatSendMessage, 15235L, 2123L);
        chatSendMessage.setChatId(121233L);
        chatSendMessage.setSenderNick("sender");
        chatSendMessage.setMessage("Hello world!");

        HelpMessage helpMessage = new HelpMessage();
        setAbstractData(chatListMessage, 1124L, 212312L);

        LoginMessage loginMessage = new LoginMessage();
        setAbstractData(loginMessage, 11241L, 212314L);
        loginMessage.setLogin("login");
        loginMessage.setPassword("password");
        loginMessage.setRepeatPassword("repeat_password");

        UserInfoMessage userInfoMessage = new UserInfoMessage();
        setAbstractData(userInfoMessage, 156L, 2567L);
        userInfoMessage.setUserId(167346L);

        UserMessage userMessage = new UserMessage();
        setAbstractData(userMessage, 12356L, 23452L);
        userMessage.setNick("nick");

        UserPassMessage userPassMessage = new UserPassMessage();
        setAbstractData(userPassMessage, 1123L, 2425333L);
        userPassMessage.setOldPassword("old_password");
        userPassMessage.setNewPassword("new_password");

        MESSAGES.put(chatCreateMessage.getType(), chatCreateMessage);
        MESSAGES.put(chatFindMessage.getType(), chatFindMessage);
        MESSAGES.put(chatHistoryMessage.getType(), chatHistoryMessage);
        MESSAGES.put(chatListMessage.getType(), chatListMessage);
        MESSAGES.put(chatSendMessage.getType(), chatSendMessage);
        MESSAGES.put(helpMessage.getType(), helpMessage);
        MESSAGES.put(loginMessage.getType(), loginMessage);
        MESSAGES.put(userInfoMessage.getType(), userInfoMessage);
        MESSAGES.put(userMessage.getType(), userMessage);
        MESSAGES.put(userPassMessage.getType(), userPassMessage);

        ChatCreateResultMessage chatCreateResultMessage = new ChatCreateResultMessage();
        setAbstractData(chatCreateResultMessage, 1124L, 2142523L);
        chatCreateResultMessage.setNewChatId(12233L);

        ChatFindResultMessage chatFindResultMessage = new ChatFindResultMessage();
        setAbstractData(chatFindResultMessage, 11222L, 234412L);
        chatFindResultMessage.setMessages(Arrays.asList("find_message_1", "find_message_2", "find_message_3"));

        ChatHistoryResultMessage chatHistoryResultMessage = new ChatHistoryResultMessage();
        setAbstractData(chatHistoryResultMessage, 1234L, 2234L);
        chatHistoryResultMessage.setMessages(Arrays.asList("history_message_1", "history_message_2", "history_message_3"));

        ChatListResultMessage chatListResultMessage = new ChatListResultMessage();
        setAbstractData(chatListResultMessage, 1145L, 2124L);
        Map<Long, List<Long>> chatData = new HashMap<>();
        chatData.put(166L, Arrays.asList(112L, 223L));
        chatData.put(233L, Arrays.asList(312L, 4234L));
        chatData.put(3666L, Arrays.asList(5123L, 6545L));
        chatListResultMessage.setChatData(chatData);

        CommandResultMessage commandResultMessage = new CommandResultMessage();
        setAbstractData(commandResultMessage, 122242L, 223423L);
        commandResultMessage.setState(CommandResultState.OK);
        commandResultMessage.setData("data");

        HelpResultMessage helpResultMessage = new HelpResultMessage();
        setAbstractData(helpResultMessage, 1678L, 2678L);
        helpResultMessage.setHelpContent(Arrays.asList("help_content_1", "help_content_2", "help_content_3"));

        LoginResultMessage loginResultMessage = new LoginResultMessage();
        setAbstractData(loginResultMessage, 1678L, 2678L);
        loginResultMessage.setLogin("login");
        loginResultMessage.setUserId(16783L);

        UserInfoResultMessage userInfoResultMessage = new UserInfoResultMessage();
        setAbstractData(userInfoResultMessage, 11234L, 2799L);
        User user = new User("login", "password_hash");
        user.setId(2451L);
        user.setNick("nick");
        userInfoResultMessage.setUser(user);

        MESSAGES.put(chatCreateResultMessage.getType(), chatCreateResultMessage);
        MESSAGES.put(chatFindResultMessage.getType(), chatFindResultMessage);
        MESSAGES.put(chatHistoryResultMessage.getType(), chatHistoryResultMessage);
        MESSAGES.put(chatListResultMessage.getType(), chatListResultMessage);
        MESSAGES.put(commandResultMessage.getType(), commandResultMessage);
        MESSAGES.put(helpResultMessage.getType(), helpResultMessage);
        MESSAGES.put(loginResultMessage.getType(), loginResultMessage);
        MESSAGES.put(userInfoResultMessage.getType(), userInfoResultMessage);
    }

    @Test
    public void testChatCreateMessage() throws ProtocolException {
        ChatCreateMessage origin = (ChatCreateMessage) MESSAGES.get(CommandType.CHAT_CREATE);
        byte[] data = protocol.encode(origin);

        ChatCreateMessage copy = (ChatCreateMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEquals(origin.getUserIds(), copy.getUserIds());

        System.out.println("testChatCreateMessage()");
    }

    @Test
    public void testChatFindMessage() throws ProtocolException {
        ChatFindMessage origin = (ChatFindMessage) MESSAGES.get(CommandType.CHAT_FIND);
        byte[] data = protocol.encode(origin);

        ChatFindMessage copy = (ChatFindMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEquals(origin.getChatId(), copy.getChatId());
        assertEquals(origin.getRegex(), copy.getRegex());

        System.out.println("testChatFindMessage()");
    }

    @Test
    public void testChatHistoryMessage() throws ProtocolException {
        ChatHistoryMessage origin = (ChatHistoryMessage) MESSAGES.get(CommandType.CHAT_HISTORY);
        byte[] data = protocol.encode(origin);

        ChatHistoryMessage copy = (ChatHistoryMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEquals(origin.getChatId(), copy.getChatId());

        System.out.println("testChatHistoryMessage()");
    }

    @Test
    public void testChatListMessage() throws ProtocolException {
        ChatListMessage origin = (ChatListMessage) MESSAGES.get(CommandType.CHAT_LIST);
        byte[] data = protocol.encode(origin);

        ChatListMessage copy = (ChatListMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);

        System.out.println("testChatListMessage()");
    }

    @Test
    public void testChatSendMessage() throws ProtocolException {
        ChatSendMessage origin = (ChatSendMessage) MESSAGES.get(CommandType.CHAT_SEND);
        byte[] data = protocol.encode(origin);

        ChatSendMessage copy = (ChatSendMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEquals(origin.getChatId(), copy.getChatId());
        assertEquals(origin.getSenderNick(), copy.getSenderNick());
        assertEquals(origin.getMessage(), copy.getMessage());

        System.out.println("testChatSendMessage()");
    }

    @Test
    public void testHelpMessage() throws ProtocolException {
        HelpMessage origin = (HelpMessage) MESSAGES.get(CommandType.HELP);
        byte[] data = protocol.encode(origin);

        HelpMessage copy = (HelpMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);

        System.out.println("testHelpMessage()");
    }

    @Test
    public void testLoginMessage() throws ProtocolException {
        LoginMessage origin = (LoginMessage) MESSAGES.get(CommandType.LOGIN);
        byte[] data = protocol.encode(origin);

        LoginMessage copy = (LoginMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEquals(origin.getLogin(), copy.getLogin());
        assertEquals(origin.getPassword(), copy.getPassword());
        assertEquals(origin.getRepeatPassword(), copy.getRepeatPassword());

        System.out.println("testLoginMessage()");
    }

    @Test
    public void testUserInfoMessage() throws ProtocolException {
        UserInfoMessage origin = (UserInfoMessage) MESSAGES.get(CommandType.USER_INFO);
        byte[] data = protocol.encode(origin);

        UserInfoMessage copy = (UserInfoMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEquals(origin.getUserId(), copy.getUserId());

        System.out.println("testUserInfoMessage()");
    }

    @Test
    public void testUserMessage() throws ProtocolException {
        UserMessage origin = (UserMessage) MESSAGES.get(CommandType.USER);
        byte[] data = protocol.encode(origin);

        UserMessage copy = (UserMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEquals(origin.getNick(), copy.getNick());

        System.out.println("testUserMessage()");
    }

    @Test
    public void testUserPassMessage() throws ProtocolException {
        UserPassMessage origin = (UserPassMessage) MESSAGES.get(CommandType.USER_PASS);
        byte[] data = protocol.encode(origin);

        UserPassMessage copy = (UserPassMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEquals(origin.getOldPassword(), copy.getOldPassword());
        assertEquals(origin.getNewPassword(), copy.getNewPassword());

        System.out.println("testUserPassMessage()");
    }


    @Test
    public void testChatCreateResultMessage() throws ProtocolException {
        ChatCreateResultMessage origin = (ChatCreateResultMessage) MESSAGES.get(CommandType.CHAT_CREATE_RESULT);
        byte[] data = protocol.encode(origin);

        ChatCreateResultMessage copy = (ChatCreateResultMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEquals(origin.getNewChatId(), copy.getNewChatId());

        System.out.println("testChatCreateResultMessage()");
    }

    @Test
    public void testChatFindResultMessage() throws ProtocolException {
        ChatFindResultMessage origin = (ChatFindResultMessage) MESSAGES.get(CommandType.CHAT_FIND_RESULT);
        byte[] data = protocol.encode(origin);

        ChatFindResultMessage copy = (ChatFindResultMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEqualsList(origin.getMessages(), copy.getMessages());

        System.out.println("testChatFindResultMessage()");
    }

    @Test
    public void testChatHistoryResultMessage() throws ProtocolException {
        ChatHistoryResultMessage origin = (ChatHistoryResultMessage) MESSAGES.get(CommandType.CHAT_HISTORY_RESULT);
        byte[] data = protocol.encode(origin);

        ChatHistoryResultMessage copy = (ChatHistoryResultMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEqualsList(origin.getMessages(), copy.getMessages());

        System.out.println("testChatHistoryResultMessage()");
    }

    @Test
    public void testChatListResultMessage() throws ProtocolException {
        ChatListResultMessage origin = (ChatListResultMessage) MESSAGES.get(CommandType.CHAT_LIST_RESULT);
        byte[] data = protocol.encode(origin);

        ChatListResultMessage copy = (ChatListResultMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);

        assertEquals(origin.getChatData().size(), copy.getChatData().size());
        for (Map.Entry<Long, List<Long>> entry : origin.getChatData().entrySet()) {
            assertEqualsList(entry.getValue(), copy.getChatData().get(entry.getKey()));
        }

        System.out.println("testChatListResultMessage()");
    }

    @Test
    public void testCommandResultMessage() throws ProtocolException {
        CommandResultMessage origin = (CommandResultMessage) MESSAGES.get(CommandType.COMMAND_RESULT);
        byte[] data = protocol.encode(origin);

        CommandResultMessage copy = (CommandResultMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEquals(origin.getState(), copy.getState());
        assertEquals(origin.getData(), copy.getData());

        System.out.println("testCommandResultMessage()");
    }

    @Test
    public void testHelpResultMessage() throws ProtocolException {
        HelpResultMessage origin = (HelpResultMessage) MESSAGES.get(CommandType.HELP_RESULT);
        byte[] data = protocol.encode(origin);

        HelpResultMessage copy = (HelpResultMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEqualsList(origin.getHelpContent(), copy.getHelpContent());

        System.out.println("testHelpResultMessage()");
    }

    @Test
    public void testLoginResultMessage() throws ProtocolException {
        LoginResultMessage origin = (LoginResultMessage) MESSAGES.get(CommandType.LOGIN_RESULT);
        byte[] data = protocol.encode(origin);

        LoginResultMessage copy = (LoginResultMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);
        assertEquals(origin.getLogin(), copy.getLogin());
        assertEquals(origin.getUserId(), copy.getUserId());

        System.out.println("testLoginResultMessage()");
    }

    @Test
    public void testUserInfoResultMessage() throws ProtocolException {
        UserInfoResultMessage origin = (UserInfoResultMessage) MESSAGES.get(CommandType.USER_INFO_RESULT);
        byte[] data = protocol.encode(origin);

        UserInfoResultMessage copy = (UserInfoResultMessage) protocol.decode(data);
        assertEqualsAbstractData(origin, copy);

        User originUser = origin.getUser();
        User copyUser = copy.getUser();
        assertEquals(originUser.getId(), copyUser.getId());
        assertEquals(originUser.getLogin(), copyUser.getLogin());
        assertEquals(originUser.getNick(), copyUser.getNick());
        assertEquals(originUser.getPasswordHash(), copyUser.getPasswordHash());

        System.out.println("testUserInfoResultMessage()");
    }
}