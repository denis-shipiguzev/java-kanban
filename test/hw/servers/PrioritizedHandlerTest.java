package hw.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import main.java.hw.managers.Managers;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Task;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PrioritizedHandlerTest {
    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer taskServer;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    public PrioritizedHandlerTest() throws IOException {
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
    public void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Test task 1",
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Test task 2",
                LocalDateTime.of(2024, 3, 22, 10, 0), Duration.ofMinutes(30));
        Task task3 = new Task("Task 3", "Test task 3",
                LocalDateTime.of(2024, 3, 22, 14, 0), Duration.ofMinutes(25));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/prioritized");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());

        Set<Task> tasksFromManager = gson.fromJson(response.body(), new TypeToken<Set<Task>>() {
        }.getType());

        assertNotNull(tasksFromManager, "Prioritized task is empty.");
        assertEquals(3, tasksFromManager.size(), "Incorrect size of prioritized tasks.");
    }
}
