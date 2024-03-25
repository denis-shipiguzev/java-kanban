package hw.manager.taskmanagers;

import main.java.hw.exceptions.ManagerSaveException;
import main.java.hw.managers.taskmanagers.FileBackedTaskManager;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tmpFile;

    @BeforeEach
    public void createFileBackTaskManager() throws IOException {
        tmpFile = File.createTempFile("test", ".csv");
        taskManager = new FileBackedTaskManager(tmpFile.toPath());
    }

    @Test
    public void shouldReturnTasksAndHistoryAfterCreating() {
        Task task = new Task("Task 1", "Test task 1",
                LocalDateTime.of(2024, 3, 5, 15, 0, 0), Duration.ofMinutes(30));
        taskManager.createTask(task);
        Epic epic = new Epic("Epic 1", "Test epic 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Test subtask 1", epic.getTaskId(),
                LocalDateTime.of(2024, 3, 5, 16, 0, 0), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask);
        taskManager.getTaskById(1);
        TaskManager testFileBackedTaskManager;
        try {
            testFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        } catch (IOException exception) {
            throw new ManagerSaveException("Error reading from file.", exception);
        }
        assertEquals(1, testFileBackedTaskManager.getTasks().size());
        assertEquals(1, testFileBackedTaskManager.getSubtasks().size());
        assertEquals(1, testFileBackedTaskManager.getEpics().size());
        assertEquals(1, testFileBackedTaskManager.getHistory().size());
    }

    @Test
    public void shouldReturnTasksAndHistoryAfterLoading() {
        File tasksForTest = new File(String.valueOf(Path.of("src/main/resources/tasksForTest.csv")));
        TaskManager testLoadFileBackedTaskManager;
        try {
            testLoadFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tasksForTest);
        } catch (IOException exception) {
            throw new ManagerSaveException("Error reading from file.", exception);
        }
        assertEquals(1, testLoadFileBackedTaskManager.getTasks().size());
        assertEquals(1, testLoadFileBackedTaskManager.getEpics().size());
        assertEquals(1, testLoadFileBackedTaskManager.getSubtasks().size());
        assertEquals(2, testLoadFileBackedTaskManager.getHistory().size());
    }

    @Test
    public void shouldReturnDoesNotThrowAfterCreateEmptyFile() throws IOException {
        File emptyFile = File.createTempFile("empty", ".csv");
        FileBackedTaskManager fileManager = new FileBackedTaskManager(emptyFile.toPath());
        assertDoesNotThrow(fileManager::save);
    }

    @Test
    public void shouldReturnEmptyAfterLoadEmptyFile() throws IOException {
        File emptyFile = File.createTempFile("empty", ".csv");
        TaskManager testLoadFileBackedTaskManager;
        try {
            testLoadFileBackedTaskManager = FileBackedTaskManager.loadFromFile(emptyFile);
        } catch (IOException exception) {
            throw new ManagerSaveException("Error reading from file.", exception);
        }
        assertEquals(0, testLoadFileBackedTaskManager.getTasks().size());
        assertEquals(0, testLoadFileBackedTaskManager.getEpics().size());
        assertEquals(0, testLoadFileBackedTaskManager.getSubtasks().size());
        assertEquals(0, testLoadFileBackedTaskManager.getHistory().size());
    }
}
