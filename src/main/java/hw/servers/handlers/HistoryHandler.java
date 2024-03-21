package main.java.hw.servers.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.servers.HttpTaskServer;
import main.java.hw.servers.handlers.enums.Endpoint;

import java.io.IOException;

import static main.java.hw.servers.handlers.EndpointResolver.getEndpoint;

public class HistoryHandler implements HttpHandler {

    protected final TaskManager taskManager;
    protected final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endpoint) {
            case GET_HISTORY: {
                handleGetHistory(exchange);
                break;
            }
            default:
                HttpResponseHandler.writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    void handleGetHistory(HttpExchange exchange) throws IOException {
        HttpResponseHandler.writeResponse(exchange,
                gson.toJson(taskManager.getHistory()),
                200);
    }
}
