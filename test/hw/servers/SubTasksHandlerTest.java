package hw.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import main.java.hw.managers.Managers;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.enums.TaskStatus;
import main.java.hw.servers.HttpTaskServer;
import main.java.hw.servers.adapters.DurationTypeAdapter;
import main.java.hw.servers.adapters.LocalDateTimeAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubTasksHandlerTest {
    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer taskServer;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    public SubTasksHandlerTest() throws IOException {
        this.taskServer = new HttpTaskServer(manager);
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void shouldReturnSubtasksAfterGetMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Test subtask 1", epic.getTaskId(),
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        manager.createSubtask(subtask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());
        List<Subtask> subtasksFromManager = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());

        assertNotNull(subtasksFromManager, "Subtasks not returned.");
        assertEquals(1, subtasksFromManager.size(), "Incorrect number of subtasks.");
        assertEquals(subtask, subtasksFromManager.get(0), "Subtasks not equals");
    }

    @Test
    public void shouldReturnSubtaskIdAfterGetMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Test subtask 1", epic.getTaskId(),
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        manager.createSubtask(subtask);
        int subtaskId = subtask.getTaskId();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());
        Subtask subtaskFromManager = gson.fromJson(response.body(), new TypeToken<Subtask>() {
        }.getType());

        assertNotNull(subtaskFromManager, "Subtask not returned.");
        assertEquals(subtask, subtaskFromManager, "Subtasks not equals");
    }

    @Test
    public void shouldCreateSubtaskAfterPostMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Test subtask 1", epic.getTaskId(),
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        String subtaskJson = gson.toJson(subtask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Subtasks not returned.");
        assertEquals(1, subtasksFromManager.size(), "Incorrect number of subtasks.");
        assertEquals(subtask.getName(), subtasksFromManager.get(0).getName(), "Subtasks not equals.");
        assertEquals(subtask.getType(), subtasksFromManager.get(0).getType(), "Subtasks not equals.");
        assertEquals(subtask.getDescription(), subtasksFromManager.get(0).getDescription(), "Subtasks not equals.");
        assertEquals(subtask.getStatus(), subtasksFromManager.get(0).getStatus(), "Subtasks not equals.");
    }

    @Test
    public void shouldUpdateSubtaskAfterPostMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Test subtask 1", epic.getTaskId(),
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        manager.createSubtask(subtask);
        subtask = new Subtask(subtask.getTaskId(), "Subtask 1", "Test subtask 1", subtask.getParentTaskId(), TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, 3, 23, 12, 0, 0), Duration.ofMinutes(30));
        String subtaskJson = gson.toJson(subtask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Subtask not returned.");
        assertEquals(subtask.getName(), subtasksFromManager.get(0).getName(), "Subtasks not equals.");
        assertEquals(subtask.getType(), subtasksFromManager.get(0).getType(), "Subtasks not equals.");
        assertEquals(subtask.getDescription(), subtasksFromManager.get(0).getDescription(), "Subtasks not equals.");
        assertEquals(TaskStatus.IN_PROGRESS, subtasksFromManager.get(0).getStatus(), "Subtasks not equals.");
    }

    @Test
    public void shouldDeleteSubtaskAfterDeleteMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Test subtask 1", epic.getTaskId(),
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        manager.createSubtask(subtask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks?id=" + subtask.getTaskId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getSubtasks().size(), "incorrect tasks size after delete");
    }
}
