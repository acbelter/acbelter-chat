package com.acbelter.chat.message.base;

import com.acbelter.chat.HashUtil;

import java.util.HashMap;
import java.util.Map;

public class UserStoreStub implements UserStore {
    private static Map<Long, User> users = new HashMap<>();


    static {
        User u0 = new User("user0", HashUtil.generateHash("0"));
        u0.setId(0L);

        User u1 = new User("user1", HashUtil.generateHash("1"));
        u1.setId(1L);

        User u2 = new User("user2", HashUtil.generateHash("2"));
        u2.setId(2L);

        User u3 = new User("user3", HashUtil.generateHash("3"));
        u3.setId(3L);

        users.put(0L, u0);
        users.put(1L, u1);
        users.put(2L, u2);
        users.put(3L, u3);
    }

    @Override
    public User addUser(User user) {
        if (user == null || user.getLogin() == null || user.getPasswordHash() == null) {
            return null;
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(String login) {
        for (User user : users.values()) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public boolean updateUser(User user) {
        return false;
    }

    @Override
    public boolean isUserExists(String login) {
        for (User user : users.values()) {
            if (user.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }
}


//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.HashMap;
//import java.util.Map;
//
//public class UserStoreStub implements UserStore {
//    private long lastUserId;
//    private String storageFilename;
//    private Map<String, User> usersMap;
//
//    public UserStoreStub(String storageFilename) {
//        this.storageFilename = storageFilename;
//        usersMap = new HashMap<>();
//
//        try {
//            loadUsersData();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadUsersData() throws IOException {
//        usersMap.clear();
//
//        if (Files.notExists(Paths.get(storageFilename))) {
//            Files.createFile(Paths.get(storageFilename));
//        }
//
//        for (String line : Files.readAllLines(Paths.get(storageFilename))) {
//            if (!line.isEmpty()) {
//                String[] data = line.split(" ");
//                if (data.length != 3) {
//                    continue;
//                }
//
//                long id = Long.parseLong(data[0]);
//                if (id > lastUserId) {
//                    lastUserId = id;
//                }
//                User newUser = new User(data[1], data[2]);
//                usersMap.put(data[1], newUser);
//            }
//        }
//    }
//
//    @Override
//    public User addUser(User user) {
//        if (user == null || user.getLogin() == null || user.getPasswordHash() == null) {
//            return null;
//        }
//
//        try {
//            String userData = ++lastUserId + user.getLogin() + " " + user.getPasswordHash() + "\n";
//            Files.write(Paths.get(storageFilename), userData.getBytes(),
//                    StandardOpenOption.APPEND);
//            usersMap.put(user.getLogin(), user);
//            user.setId(lastUserId);
//        } catch (IOException e) {
//            return null;
//        }
//        return user;
//    }
//
//    @Override
//    public boolean updateUser(User user) {
//        if (user == null ||
//                user.getLogin() == null ||
//                user.getPasswordHash() == null ||
//                !usersMap.containsKey(user.getLogin())) {
//            return false;
//        }
//
//
//        User oldUser = usersMap.put(user.getLogin(), user);
//        try {
//            Files.write(Paths.get(storageFilename), getUserDataString().getBytes());
//        } catch (IOException e) {
//            usersMap.put(oldUser.getLogin(), oldUser);
//            return false;
//        }
//
//        return true;
//    }
//
//    private String getUserDataString() {
//        StringBuilder builder = new StringBuilder();
//        for (User user : usersMap.values()) {
//            builder.append(user.getLogin()).append(" ").append(user.getPasswordHash()).append("\n");
//        }
//        return builder.toString();
//    }
//
//    @Override
//    public boolean isUserExists(String login) {
//        return login != null && usersMap.containsKey(login);
//    }
//
//    @Override
//    public User getUser(String login) {
//        return usersMap.get(login);
//    }
//}