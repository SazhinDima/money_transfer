package ru.sazhin;

public class Application {

    public static void main(String[] args) {
        Database.getInstance().createTables();
        Database.getInstance().initData();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> Database.getInstance().closeConnection()));

        WebServer.getInstance().start();
    }
}
