package main.java.hw.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import main.java.hw.managers.Managers;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.servers.adapters.DurationTypeAdapter;
import main.java.hw.servers.adapters.LocalDateTimeAdapter;
import main.java.hw.servers.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private final Gson gson;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.gson = createGson();
        this.server = createHttpServer();
        createContexts();
    }

    private Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
    }

    private HttpServer createHttpServer() throws IOException {
        return HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    private void createContexts() {
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/subtasks", new SubTasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public void start() {
        server.start();
        System.out.println("HTTP-server started port: " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-server stopped port: " + PORT);
    }
    public static void main(String[] args) {
        try {
            TaskManager taskManager = Managers.getDefault();
            HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
            httpTaskServer.start();
        } catch (IOException e) {
            System.err.println("Failed to start HTTP server: " + e.getMessage());
        }
    }
}
