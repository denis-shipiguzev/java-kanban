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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TasksHandlerTest {
    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer taskServer;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    public TasksHandlerTest() throws IOException {
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
    public void shouldReturnTasks() throws IOException, InterruptedException {
        Task task = new Task("task", "taskDescription",
                LocalDateTime.of(2024, 3, 5, 0, 0), Duration.ofMinutes(5));
        manager.createTask(task);
        int taskId = task.getTaskId();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());

        Task taskFromManager = tasksFromManager.get(0);
        assertEquals(taskId, taskFromManager.getTaskId(), "incorrect task id");
        assertEquals(task.getName(), taskFromManager.getName(), "incorrect task name");
        assertEquals(task.getDescription(), taskFromManager.getDescription(), "incorrect task description");
        assertEquals(task.getDuration().toString(), taskFromManager.getDuration().toString(), "incorrect task duration");
        assertEquals(task.getStartTime().toString(), taskFromManager.getStartTime().toString(), "incorrect task start time");
    }
}
