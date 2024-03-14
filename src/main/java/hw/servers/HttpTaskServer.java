package main.java.hw.servers;

import com.sun.net.httpserver.HttpServer;
import main.java.hw.servers.handlers.TasksHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(HttpServer server) {
        this.server = server;
    }

    public static void main(String[] args) {

    }

    void start() {
        try {
            server.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/tasks", new TasksHandler());
/*        server.createContext("/subtasks", new SubTasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritozedHandler());
 */
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    void stop() {
        server.stop(0);
    }
}
