package hw.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import main.java.hw.managers.Managers;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
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

public class EpicsHandlerTest {
    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer taskServer;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    public EpicsHandlerTest() throws IOException {
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
    public void shouldReturnEpicsAfterGetMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");
        manager.createEpic(epic);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());
        List<Epic> epicsFromManager = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertNotNull(epicsFromManager, "Epics not returned.");
        assertEquals(1, epicsFromManager.size(), "Incorrect number of epics.");
        assertEquals(epic, epicsFromManager.get(0), "Epics not equals");
    }

    @Test
    public void shouldReturnEpicsIdAfterGetMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");
        manager.createEpic(epic);
        int epicId = epic.getTaskId();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epicId);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());
        Epic epicFromManager = gson.fromJson(response.body(), new TypeToken<Epic>() {
        }.getType());

        assertNotNull(epicFromManager, "Epic not returned.");
        assertEquals(epic, epicFromManager, "Epics not equals");
    }

    @Test
    public void shouldReturnEpicsSubtasksAfterGetMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Test subtask 1", epic.getTaskId(),
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        manager.createSubtask(subtask);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epic.getTaskId() + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());
        List<Subtask> subtasksListFromManager = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(manager.getEpicSubtasks(epic.getTaskId()), subtasksListFromManager, "Epic Subtasks not equals.");
    }
}
