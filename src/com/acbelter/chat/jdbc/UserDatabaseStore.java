package com.acbelter.chat.jdbc;

import com.acbelter.chat.message.base.User;
import com.acbelter.chat.message.base.UserStore;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDatabaseStore implements UserStore {
    static Logger log = LoggerFactory.getLogger(UserDatabaseStore.class);
    private Connection connection;
    private QueryExecutor queryExecutor;

    public UserDatabaseStore() {
        connection = DatabaseConnector.getInstance().getConnection();
        queryExecutor = new QueryExecutor(connection);
    }

    @Override
    public User addUser(User user) {
        if (user == null || user.getLogin() == null || user.getPasswordHash() == null) {
            return null;
        }

        if (getUser(user.getLogin()) != null) {
            return null;
        }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO user_table (login, password_hash) values ('")
                    .append(StringEscapeUtils.escapeSql(user.getLogin()))
                    .append("', '")
                    .append(StringEscapeUtils.escapeSql(user.getPasswordHash()))
                    .append("');");
            List<Long> ids = queryExecutor.execUpdate(sb.toString());
            if (ids.size() == 1) {
                user.setId(ids.get(0));
                log.info("Add user to db " + user);
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUser(String login) {
        if (login == null) {
            return null;
        }

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, StringEscapeUtils.escapeSql(login));

        try {
            User user = queryExecutor.execQuery("SELECT * FROM user_table WHERE login = ? LIMIT 1;", prepared, (r) -> {
                if (r.next()) {
                    User u = new User(r.getString(2), r.getString(3));
                    u.setId(r.getLong(1));
                    return u;
                }
                return null;
            });
            log.info("Get user from db " + user);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserById(Long id) {
        if (id == null) {
            return null;
        }

        Map<Integer, Object> prepared = new HashMap<>();
        prepared.put(1, id);

        try {
            User user = queryExecutor.execQuery("SELECT * FROM user_table WHERE id = ? LIMIT 1;", prepared, (r) -> {
                if (r.next()) {
                    User u = new User(r.getString(2), r.getString(3));
                    u.setId(r.getLong(1));
                    return u;
                }
                return null;
            });
            log.info("Get user from db " + user);
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateUser(User user) {
        if (user == null) {
            return false;
        }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE user_table SET login = '")
                    .append(StringEscapeUtils.escapeSql(user.getLogin()))
                    .append("', password_hash = '")
                    .append(StringEscapeUtils.escapeSql(user.getPasswordHash()))
                    .append("' WHERE id = ").append(user.getId()).append(";");
            List<Long> ids = queryExecutor.execUpdate(sb.toString());
            if (!ids.isEmpty()) {
                log.info("Update user " + user);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
