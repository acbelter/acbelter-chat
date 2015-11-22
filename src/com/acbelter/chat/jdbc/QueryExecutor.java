package com.acbelter.chat.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryExecutor {
    private Connection connection;

    public QueryExecutor(Connection connection) {
        this.connection = connection;
    }

    // Простой запрос
    public <T> T execQuery(String query, ResultHandler<T> handler) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(query);
        ResultSet result = stmt.getResultSet();
        T data = handler.handle(result);
        result.close();
        stmt.close();

        return data;
    }

    // Подготовленный запрос
    public <T> T execQuery(String query, Map<Integer, Object> preparedArgs, ResultHandler<T> handler) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        for (Map.Entry<Integer, Object> entry : preparedArgs.entrySet()) {
            stmt.setObject(entry.getKey(), entry.getValue());
        }
        ResultSet rs = stmt.executeQuery();
        T data = handler.handle(rs);
        rs.close();
        stmt.close();
        return data;
    }

    // Простой update-запрос
    public List<Long> execUpdate(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

        ResultSet rs = stmt.getGeneratedKeys();
        List<Long> data = new ArrayList<>();
        while (rs.next()) {
            data.add(rs.getLong(1));
        }

        rs.close();
        stmt.close();
        return data;
    }

    // Подготовленный update-запрос
    public List<Long> execUpdate(String query, Map<Integer, Object> preparedArgs) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        for (Map.Entry<Integer, Object> entry : preparedArgs.entrySet()) {
            stmt.setObject(entry.getKey(), entry.getValue());
        }
        stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

        ResultSet rs = stmt.getGeneratedKeys();
        List<Long> data = new ArrayList<>();
        while (rs.next()) {
            data.add(rs.getLong(1));
        }

        rs.close();
        stmt.close();
        return data;
    }
}
