package com.acbelter.chat.jdbc;

import org.postgresql.ds.PGPoolingDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnector {
    private static volatile DatabaseConnector instance;
    private PGPoolingDataSource source;

    public static DatabaseConnector getInstance() {
        DatabaseConnector localInstance = instance;
        if (localInstance == null) {
            synchronized (DatabaseConnector.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DatabaseConnector();
                }
            }
        }
        return localInstance;
    }

    private DatabaseConnector() {
        try {
            Class.forName("org.postgresql.Driver");

            source = new PGPoolingDataSource();
            source.setDataSourceName("acbelter_chat_db");
            source.setServerName("178.62.140.149");
            source.setDatabaseName("acbelter");
            source.setUser("senthil");
            source.setPassword("ubuntu");
            source.setMaxConnections(10);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            return source.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
