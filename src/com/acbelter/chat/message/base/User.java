package com.acbelter.chat.message.base;

//public class User {
//    private Long id;
//    private String name;
//    private String pass;
//
//    public User(String name, String pass) {
//        this.name = name;
//        this.pass = pass;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getPasswordHash() {
//        return pass;
//    }
//
//    public void setPasswordHash(String pass) {
//        this.pass = pass;
//    }
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                '}';
//    }
//}

public class User {
    protected Long id;
    protected String login;
    protected String passwordHash;
    protected String nick;

    public User(String login) {
        this.login = login;
        this.nick = login;
    }

    public User(String login, String passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.nick = login;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void resetNick() {
        nick = login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return login.equals(user.login);
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", nick='" + nick + '\'' +
                '}';
    }
}
