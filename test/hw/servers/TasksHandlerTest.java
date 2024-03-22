package hw.servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import main.java.hw.managers.Managers;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Task;
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
    public void shouldReturnTasksAfterGetMethod() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Test task 1",
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        manager.createTask(task);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertNotNull(tasksFromManager, "Tasks not returned.");
        assertEquals(1, tasksFromManager.size(), "Incorrect number of tasks.");
        assertEquals(task, tasksFromManager.get(0), "Tasks not equals");
    }

    @Test
    public void shouldReturnTaskIdAfterGetMethod() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Test task 1",
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        manager.createTask(task);
        int taskId = task.getTaskId();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + taskId);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());
        Task taskFromManager = gson.fromJson(response.body(), new TypeToken<Task>() {
        }.getType());

        assertNotNull(taskFromManager, "Task not returned.");
        assertEquals(task, taskFromManager, "Tasks not equals");
    }

    @Test
    public void shouldCreateTaskAfterPostMethod() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Test task 1",
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Tasks not returned.");
        assertEquals(1, tasksFromManager.size(), "Incorrect number of tasks.");
        assertEquals(task.getName(), tasksFromManager.get(0).getName(), "Tasks not equals.");
        assertEquals(task.getType(), tasksFromManager.get(0).getType(), "Tasks not equals.");
        assertEquals(task.getDescription(), tasksFromManager.get(0).getDescription(), "Tasks not equals.");
        assertEquals(task.getStatus(), tasksFromManager.get(0).getStatus(), "Tasks not equals.");
    }


    @Test
    public void shouldUpdateTaskAfterPostMethod() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Test task 1",
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        manager.createTask(task);
        task = new Task(task.getTaskId(), "Task 1", "Test task 1", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, 3, 22, 12, 0, 0), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());
        Task taskFromManager = manager.getTaskById(task.getTaskId());

        assertNotNull(taskFromManager, "Task not returned.");
        assertEquals(task.getName(), taskFromManager.getName(), "Tasks not equals.");
        assertEquals(task.getType(), taskFromManager.getType(), "Tasks not equals.");
        assertEquals(task.getDescription(), taskFromManager.getDescription(), "Tasks not equals.");
        assertEquals(TaskStatus.IN_PROGRESS, taskFromManager.getStatus(), "Tasks not equals.");
    }

    @Test
    public void shouldDeleteTaskAfterDeleteMethod() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Test task 1",
                LocalDateTime.of(2024, 3, 22, 12, 0), Duration.ofMinutes(30));
        manager.createTask(task);
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks?id=" + task.getTaskId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getTasks().size(), "incorrect tasks size after delete");
    }
}
