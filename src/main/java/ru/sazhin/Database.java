package ru.sazhin;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.sazhin.model.Account;
import ru.sazhin.model.Transaction;
import ru.sazhin.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class Database {

    public static class SingletonHolder {
        public static final Database HOLDER_INSTANCE = new Database();
    }

    public static Database getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

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
                connectionSource = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void createTables() {
        try {
            TableUtils.createTableIfNotExists(Database.getInstance().getConnection(), Account.class);
            TableUtils.createTableIfNotExists(Database.getInstance().getConnection(), User.class);
            TableUtils.createTableIfNotExists(Database.getInstance().getConnection(), Transaction.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initData() {
        try {
            Dao<User, Long> userDao = DaoManager.createDao(Database.getInstance().getConnection(), User.class);
            User dima = new User("Dima");
            userDao.create(dima);
            User vasya = new User("Vasya");
            userDao.create(vasya);

            Dao<Account, Long> accountDao = DaoManager.createDao(Database.getInstance().getConnection(), Account.class);
            Account accDima = new Account(dima, BigDecimal.valueOf(100));
            accountDao.create(accDima);
            Account accVasya = new Account(vasya, BigDecimal.valueOf(200));
            accountDao.create(accVasya);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) {
        try {
            return DaoManager.createDao(Database.getInstance().getConnection(), clazz);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
