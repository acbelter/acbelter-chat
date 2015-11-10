package com.acbelter.chat.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Обобщенный интерфейс обработки результата
 */
public interface ResultHandler<T> {
    T handle(ResultSet resultSet) throws SQLException;
}
