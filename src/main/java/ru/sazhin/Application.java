package ru.sazhin;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import ru.sazhin.model.Account;
import ru.sazhin.model.Transaction;
import ru.sazhin.model.User;
import ru.sazhin.service.impl.AccountServiceImpl;
import ru.sazhin.service.impl.TransactionServiceImpl;
import ru.sazhin.service.impl.UserServiceImpl;
import ru.sazhin.utils.Connection;

import java.math.BigDecimal;
import java.util.Arrays;

public class Application {

    public static void main(String[] args) throws Exception {
        TableUtils.createTableIfNotExists(Connection.INSTANCE.getConnection(), Account.class);
        TableUtils.createTableIfNotExists(Connection.INSTANCE.getConnection(), User.class);
        TableUtils.createTableIfNotExists(Connection.INSTANCE.getConnection(), Transaction.class);

        Dao<User, Long> userDao = DaoManager.createDao(Connection.INSTANCE.getConnection(), User.class);
        User dima = new User("Dima");
        userDao.create(dima);
        User vasya = new User("Vasya");
        userDao.create(vasya);

        Dao<Account, Long> accountDao = DaoManager.createDao(Connection.INSTANCE.getConnection(), Account.class);
        Account accDima = new Account(dima, BigDecimal.valueOf(100));
        accountDao.create(accDima);
        Account accVasya = new Account(vasya, BigDecimal.valueOf(200));
        accountDao.create(accVasya);

        Runtime.getRuntime().addShutdownHook(new Thread(() ->  Connection.INSTANCE.closeConnection()));

        startService();
    }

    private static void startService() throws Exception {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitParameter("jersey.config.server.provider.classnames", String.join(",", Arrays.asList(
                UserServiceImpl.class.getCanonicalName(),
                AccountServiceImpl.class.getCanonicalName(),
                TransactionServiceImpl.class.getCanonicalName()
                )));

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}
