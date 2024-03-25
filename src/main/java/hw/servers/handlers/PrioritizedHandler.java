package main.java.hw.servers.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.servers.handlers.enums.Endpoint;

import java.io.IOException;

import static main.java.hw.servers.handlers.EndpointResolver.getEndpoint;

public class PrioritizedHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        if (endpoint == Endpoint.GET_PRIORITIZED) {
            handleGetPrioritizedTasks(exchange);
        } else {
            HttpHandlerUtil.writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        HttpHandlerUtil.writeResponse(exchange,
                gson.toJson(taskManager.getPrioritizedTasks()),
                200);
    }
}
