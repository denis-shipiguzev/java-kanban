package hw.manager;

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

public class FileBackedTaskManagerTest {
    private TaskManager fileBackedTaskManager;
    private File tmpFile;

    @BeforeEach
    public void createFileBackTaskManager() throws IOException {
        tmpFile = File.createTempFile("test", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(tmpFile.toPath());
    }

    @Test
    public void shouldReturnTasksAndHistoryAfterCreating() {
        Task task = new Task("Task 1", "Test task 1");
        fileBackedTaskManager.addTask(task);
        Epic epic = new Epic("Epic 1", "Test epic 1");
        fileBackedTaskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Test subtask 1", epic.getTaskId());
        fileBackedTaskManager.addSubTask(subtask);
        fileBackedTaskManager.getTaskByTaskId(1);
        TaskManager testFileBackedTaskManager;
        try {
            testFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        assertEquals(1, testFileBackedTaskManager.getAllTasks().size());
        assertEquals(1, testFileBackedTaskManager.getAllSubTasks().size());
        assertEquals(1, testFileBackedTaskManager.getAllEpics().size());
        assertEquals(1, testFileBackedTaskManager.getHistory().size());
    }

    @Test
    public void shouldReturnTasksAndHistoryAfterLoading() {
        File tasksForTest = new File(String.valueOf(Path.of("src/main/resources/tasksForTest.csv")));
        TaskManager testLoadFileBackedTaskManager;
        try {
            testLoadFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tasksForTest);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        assertEquals(1, testLoadFileBackedTaskManager.getAllTasks().size());
        assertEquals(1, testLoadFileBackedTaskManager.getAllEpics().size());
        assertEquals(1, testLoadFileBackedTaskManager.getAllSubTasks().size());
        assertEquals(2, testLoadFileBackedTaskManager.getHistory().size());
    }

    @Test
    public void should() {

    }
}
