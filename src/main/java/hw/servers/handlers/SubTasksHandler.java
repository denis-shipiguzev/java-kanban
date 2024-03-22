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
    private final TaskManager taskManager;
    private final Gson gson;

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
                HttpHandlerUtil.writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        HttpHandlerUtil.writeResponse(exchange,
                gson.toJson(taskManager.getSubtasks()),
                200);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Optional<Integer> taskIdOpt = Optional.of(Integer.parseInt(pathParts[2]));
        int taskId = taskIdOpt.get();
        Subtask subtask = taskManager.getSubtaskById(taskId);
        if (subtask != null) {
            HttpHandlerUtil.writeResponse(exchange, gson.toJson(subtask, Subtask.class), 200);
        } else {
            HttpHandlerUtil.writeResponse(exchange, "Пост с идентификатором " + taskId + " не найден", 404);
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        try {
            boolean isExists = taskManager.getSubtasks().stream()
                    .anyMatch(existingSubtask -> existingSubtask.getTaskId() == subtask.getTaskId());
            if (!isExists) {
                taskManager.createSubtask(subtask);
                HttpHandlerUtil.writeResponse(exchange, "Добавление успешно", 201);
            } else {
                taskManager.updateSubtask(subtask);
                HttpHandlerUtil.writeResponse(exchange, "Изменение успешно", 201);
            }
        } catch (IllegalStateException e) {
            HttpHandlerUtil.writeResponse(exchange, "Ошибка: Задачи пересекаются по времени выполнения", 406);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        int taskId = HttpHandlerUtil.extractTaskIdFromQuery(query);
        if (taskId != -1) {
            taskManager.deleteSubtaskById(taskId);
            HttpHandlerUtil.writeResponse(exchange, "Удаление успешно", 200);
        } else {
            HttpHandlerUtil.writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        }
    }
}
