package main.java.hw.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import main.java.hw.managers.Managers;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Task;
import main.java.hw.servers.adapters.DurationTypeAdapter;
import main.java.hw.servers.adapters.LocalDateTimeAdapter;
import main.java.hw.servers.adapters.TaskAdapter;
import main.java.hw.servers.handlers.TasksHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    protected static TaskManager taskManager = Managers.getDefault();

    public HttpTaskServer() {
    }

    public static void main(String[] args) {
        try {
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void start() throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
//                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .create();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(taskManager,gson));
/*        server.createContext("/subtasks", new SubTasksHandler());
        server.createContext("/epics", new EpicsHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritozedHandler());
 */
        server.start();
        System.out.println("HTTP-server started port: " + PORT);
    }

    private static void stop(HttpServer server) {
        server.stop(0);
        System.out.println("HTTP-server stopped port: " + PORT);
    }
}
