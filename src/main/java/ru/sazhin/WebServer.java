package ru.sazhin;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import ru.sazhin.service.impl.AccountServiceImpl;
import ru.sazhin.service.impl.TransactionServiceImpl;
import ru.sazhin.service.impl.UserServiceImpl;

import java.util.Arrays;

public class WebServer {

    public static class SingletonHolder {
        public static final WebServer HOLDER_INSTANCE = new WebServer();
    }

    public static WebServer getInstance() {
        return WebServer.SingletonHolder.HOLDER_INSTANCE;
    }

    public void start() {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            server.destroy();
        }
    }
}
