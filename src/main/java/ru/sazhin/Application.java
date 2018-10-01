package ru.sazhin;

import com.j256.ormlite.table.TableUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import ru.sazhin.model.Account;
import ru.sazhin.model.Transaction;
import ru.sazhin.model.User;
import ru.sazhin.utils.Connection;

public class Application {

    public static void main(String[] args) throws Exception {
        TableUtils.createTableIfNotExists(Connection.INSTANCE.getConnection(), Account.class);
        TableUtils.createTableIfNotExists(Connection.INSTANCE.getConnection(), User.class);
        TableUtils.createTableIfNotExists(Connection.INSTANCE.getConnection(), Transaction.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() ->  Connection.INSTANCE.closeConnection()));

        startService();
    }

    private static void startService() throws Exception {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}
