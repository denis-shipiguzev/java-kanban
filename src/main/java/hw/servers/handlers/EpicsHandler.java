package main.java.hw.servers.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.servers.handlers.enums.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static main.java.hw.servers.handlers.EndpointResolver.getEndpoint;

import main.java.hw.servers.HttpTaskServer;

public class EpicsHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager taskManager;
    protected final Gson gson;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GET_EPICBYID: {
                handleGetEpicById(exchange);
                break;
            }
            case GET_EPICSUBTASKS: {
                handleGetEpicSubtasks(exchange);
                break;
            }
            case POST_EPIC: {
                handlePostEpic(exchange);
                break;
            }
            case DELETE_EPIC: {
                handleDeleteEpic(exchange);
                break;
            }
            default:
                HttpResponseHandler.writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    void handleGetEpics(HttpExchange exchange) throws IOException {
        HttpResponseHandler.writeResponse(exchange,
                gson.toJson(taskManager.getEpics()),
                200);
    }

    void handleGetEpicById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Optional<Integer> taskIdOpt = Optional.of(Integer.parseInt(pathParts[2]));
        int taskId = taskIdOpt.get();
        Epic epic = taskManager.getEpicById(taskId);
        if (epic != null) {
            HttpResponseHandler.writeResponse(exchange, gson.toJson(epic, Epic.class), 200);
        } else {
            HttpResponseHandler.writeResponse(exchange, "Пост с идентификатором " + taskId + " не найден", 404);
        }
    }
    void handleGetEpicSubtasks(HttpExchange exchange) throws IOException{
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Optional<Integer> taskIdOpt = Optional.of(Integer.parseInt(pathParts[2]));
        int taskId = taskIdOpt.get();
        List<Subtask> subtaskList = taskManager.getEpicSubtasks(taskId);
        if (!subtaskList.isEmpty()) {
            HttpResponseHandler.writeResponse(exchange, gson.toJson(subtaskList), 200);
        } else {
            HttpResponseHandler.writeResponse(exchange, "Пост с идентификатором " + taskId + " не найден", 404);
        }
    }

    void handlePostEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        System.out.println(body);
        Epic epic = gson.fromJson(body, Epic.class);
        System.out.println(epic);
        try {
            boolean isExists = taskManager.getEpics().stream()
                    .anyMatch(existingEpic -> existingEpic.getTaskId() == epic.getTaskId());
            if (!isExists) {
                taskManager.createEpic(epic);
                HttpResponseHandler.writeResponse(exchange, "Добавление успешно", 201);
            } else {
                taskManager.updateEpic(epic);
                HttpResponseHandler.writeResponse(exchange, "Изменение успешно", 201);
            }
        } catch (IllegalStateException e) {
            HttpResponseHandler.writeResponse(exchange, "Ошибка: Задачи пересекаются по времени выполнения", 406);
        }
    }

    void handleDeleteEpic(HttpExchange exchange) throws IOException {
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
            taskManager.deleteEpicById(taskId);
            HttpResponseHandler.writeResponse(exchange, "Удаление успешно", 200);
        } else {
            HttpResponseHandler.writeResponse(exchange, "Некорректный идентификатор задачи", 400);
        }
    }
}

