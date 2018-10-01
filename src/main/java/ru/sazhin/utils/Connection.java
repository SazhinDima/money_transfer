package ru.sazhin.utils;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.SQLException;

public class Connection {

    public static final Connection INSTANCE = new Connection();

    private ConnectionSource connectionSource;

    public ConnectionSource getConnection() {
        if (connectionSource == null) {
            try {
                connectionSource = new JdbcPooledConnectionSource("jdbc:h2:mem:transaction");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connectionSource;
    }

    public void closeConnection() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
