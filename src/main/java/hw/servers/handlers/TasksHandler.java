package main.java.hw.servers.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.gson.*;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Task;
import main.java.hw.servers.HttpTaskServer;
import main.java.hw.servers.handlers.enums.Endpoint;

import static main.java.hw.servers.handlers.EndpointResolver.getEndpoint;

public class TasksHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager taskManager;
    protected final Gson gson;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GET_TASKBYID: {
                handleGetTaskById(exchange);
                break;
            }
            case POST_TASK: {
                handlePostTask(exchange);
                break;
            }
            case DELETE_TASK: {
                handleDeleteTask(exchange);
                break;
            }
            default:
                HttpResponseHandler.writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    void handleGetTasks(HttpExchange exchange) throws IOException {
        HttpResponseHandler.writeResponse(exchange,
                gson.toJson(taskManager.getTasks()),
                200);
    }

    void handleGetTaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Optional<Integer> taskIdOpt = Optional.of(Integer.parseInt(pathParts[2]));
        int taskId = taskIdOpt.get();
        Task task = taskManager.getTaskById(taskId);
        if (task != null) {
            HttpResponseHandler.writeResponse(exchange, gson.toJson(task, Task.class), 200);
        } else {
            HttpResponseHandler.writeResponse(exchange, "Пост с идентификатором " + taskId + " не найден", 404);
        }
    }

    void handlePostTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(body, Task.class);
        try {
            boolean isExists = taskManager.getTasks().stream()
                    .anyMatch(existingTask -> existingTask.getTaskId() == task.getTaskId());
            if (!isExists) {
                taskManager.createTask(task);
                HttpResponseHandler.writeResponse(exchange, "Добавление успешно", 201);
            } else {
                taskManager.updateTask(task);
                HttpResponseHandler.writeResponse(exchange, "Изменение успешно", 201);
            }
        } catch (IllegalStateException e) {
            HttpResponseHandler.writeResponse(exchange, "Ошибка: Задачи пересекаются по времени выполнения", 406);
        }
    }

    void handleDeleteTask(HttpExchange exchange) throws IOException {
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
            taskManager.deleteTaskById(taskId);
            HttpResponseHandler.writeResponse(exchange, "Удаление успешно", 200);
        } else {
            HttpResponseHandler.writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        }
    }
}
