package main.java.hw.servers.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.gson.*;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Task;

public class TasksHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager taskManager;
    protected final Gson gson;

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
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 & pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_TASK;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASK;
            }
        } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKBYID;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    void handleGetTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange,
                gson.toJson(taskManager.getTasks()),
                200);
    }
    void handleGetTaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Optional<Integer> taskIdOpt = Optional.of(Integer.parseInt(pathParts[2]));
        int taskId = taskIdOpt.get();
        Task task = taskManager.getTaskById(taskId);
        if (task != null) {
            writeResponse(exchange, gson.toJson(task, Task.class), 200);
        } else {
            writeResponse(exchange, "Пост с идентификатором " + taskId + " не найден", 404);
        }
    }

    void handlePostTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(body, Task.class);
        boolean isExists = taskManager.getTasks().stream()
                .anyMatch(existingTask -> existingTask.getTaskId() == task.getTaskId());
        if (!isExists) {
            taskManager.createTask(task);
            writeResponse(exchange, "Добавление успешно", 200);
        } else {
                taskManager.updateTask(task);
                writeResponse(exchange, "Изменение успешно", 200);
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
                }
            }
        }

        if (taskId != -1) {
            taskManager.deleteTaskById(taskId);
            writeResponse(exchange, "Удаление успешно", 200);
        } else {
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        }
    }

    enum Endpoint {GET_TASKS, GET_TASKBYID, POST_TASK, DELETE_TASK, UNKNOWN}
}
