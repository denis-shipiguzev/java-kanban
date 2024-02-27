package hw.manager;

import main.java.hw.managers.taskmanagers.FileBackedTaskManager;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileBackedTaskManagerTest {
    TaskManager fileBackedTaskManager;
    File tmpFile;
    File tasksForTest;

    @BeforeEach
    public void createFileBackTaskManager() throws IOException {
        tmpFile = File.createTempFile("test", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(tmpFile.toPath());
    }

    @AfterEach
    public void dropFileBackTaskManager() throws IOException {
        fileBackedTaskManager.removeAllEpics();
        fileBackedTaskManager.removeAllSubTasks();
        fileBackedTaskManager.removeAllTasks();
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, testFileBackedTaskManager.getAllTasks().size());
        assertEquals(1, testFileBackedTaskManager.getAllSubTasks().size());
        assertEquals(1, testFileBackedTaskManager.getAllEpics().size());
        assertEquals(1, testFileBackedTaskManager.getHistory().size());
    }

    @Test
    public void shouldReturnTasksAndHistoryAfterLoading() {

        tasksForTest = new File(String.valueOf(Path.of("src/main/resources/tasksForTest.csv")));
        try {
            fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tasksForTest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(1, fileBackedTaskManager.getAllTasks().size());
        assertEquals(1, fileBackedTaskManager.getAllEpics().size());
        assertEquals(1, fileBackedTaskManager.getAllSubTasks().size());
        assertEquals(2, fileBackedTaskManager.getHistory().size());
    }
}
