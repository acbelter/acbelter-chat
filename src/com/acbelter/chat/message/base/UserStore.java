package com.acbelter.chat.message.base;

public interface UserStore {
    User addUser(User user);
    User getUser(String login);
    User getUserById(Long id);
    boolean updateUser(User user);
}
