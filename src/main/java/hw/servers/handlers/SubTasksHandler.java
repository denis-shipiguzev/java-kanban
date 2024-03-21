package main.java.hw.servers.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Subtask;
import main.java.hw.servers.handlers.enums.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static main.java.hw.servers.handlers.EndpointResolver.getEndpoint;

public class SubTasksHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager taskManager;
    protected final Gson gson;

    public SubTasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS: {
                handleGetSubtasks(exchange);
                break;
            }
            case GET_SUBTASKBYID: {
                handleGetSubtaskById(exchange);
                break;
            }
            case POST_SUBTASK: {
                handlePostSubtask(exchange);
                break;
            }
            case DELETE_SUBTASK: {
                handleDeleteSubtask(exchange);
                break;
            }
            default:
                HttpResponseHandler.writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    void handleGetSubtasks(HttpExchange exchange) throws IOException {
        HttpResponseHandler.writeResponse(exchange,
                gson.toJson(taskManager.getSubtasks()),
                200);
    }

    void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Optional<Integer> taskIdOpt = Optional.of(Integer.parseInt(pathParts[2]));
        int taskId = taskIdOpt.get();
        Subtask subtask = taskManager.getSubtaskById(taskId);
        if (subtask != null) {
            HttpResponseHandler.writeResponse(exchange, gson.toJson(subtask, Subtask.class), 200);
        } else {
            HttpResponseHandler.writeResponse(exchange, "Пост с идентификатором " + taskId + " не найден", 404);
        }
    }

    void handlePostSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        try {
            boolean isExists = taskManager.getSubtasks().stream()
                    .anyMatch(existingSubtask -> existingSubtask.getTaskId() == subtask.getTaskId());
            if (!isExists) {
                taskManager.createSubtask(subtask);
                HttpResponseHandler.writeResponse(exchange, "Добавление успешно", 200);
            } else {
                taskManager.updateSubtask(subtask);
                HttpResponseHandler.writeResponse(exchange, "Изменение успешно", 200);
            }
        } catch (IllegalStateException e) {
            HttpResponseHandler.writeResponse(exchange, "Ошибка: Задачи пересекаются по времени выполнения", 406);
        }
    }

    void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String[] queryParams = query.split("&");
        int taskId = -1;
        for (String param : queryParams) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("id")) {
                try {
                    taskId = Integer.parseInt(keyValue[1]);
                    break;
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (taskId != -1) {
            taskManager.deleteSubtaskById(taskId);
            HttpResponseHandler.writeResponse(exchange, "Удаление успешно", 200);
        } else {
            HttpResponseHandler.writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        }
    }
}
