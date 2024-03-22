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
import main.java.hw.servers.handlers.enums.Endpoint;

import static main.java.hw.servers.handlers.EndpointResolver.getEndpoint;

public class TasksHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
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
                HttpHandlerUtil.writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        HttpHandlerUtil.writeResponse(exchange,
                gson.toJson(taskManager.getTasks()),
                200);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Optional<Integer> taskIdOpt = Optional.of(Integer.parseInt(pathParts[2]));
        int taskId = taskIdOpt.get();
        Task task = taskManager.getTaskById(taskId);
        if (task != null) {
            HttpHandlerUtil.writeResponse(exchange, gson.toJson(task, Task.class), 200);
        } else {
            HttpHandlerUtil.writeResponse(exchange, "Пост с идентификатором " + taskId + " не найден", 404);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(body, Task.class);
        try {
            boolean isExists = taskManager.getTasks().stream()
                    .anyMatch(existingTask -> existingTask.getTaskId() == task.getTaskId());
            if (!isExists) {
                taskManager.createTask(task);
                HttpHandlerUtil.writeResponse(exchange, "Добавление успешно", 201);
            } else {
                taskManager.updateTask(task);
                HttpHandlerUtil.writeResponse(exchange, "Изменение успешно", 201);
            }
        } catch (IllegalStateException e) {
            HttpHandlerUtil.writeResponse(exchange, "Ошибка: Задачи пересекаются по времени выполнения", 406);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        int taskId = HttpHandlerUtil.extractTaskIdFromQuery(query);
        if (taskId != -1) {
            taskManager.deleteTaskById(taskId);
            HttpHandlerUtil.writeResponse(exchange, "Удаление успешно", 200);
        } else {
            HttpHandlerUtil.writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        }
    }
}
