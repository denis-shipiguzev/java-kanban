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

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tmpFile;

    @BeforeEach
    public void createFileBackTaskManager() throws IOException {
        tmpFile = File.createTempFile("test", ".csv");
        taskManager = new FileBackedTaskManager(tmpFile.toPath());
    }

    @Test
    public void shouldReturnTasksAndHistoryAfterCreating() {
        Task task = new Task("Task 1", "Test task 1");
        taskManager.addTask(task);
        Epic epic = new Epic("Epic 1", "Test epic 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Test subtask 1", epic.getTaskId());
        taskManager.addSubTask(subtask);
        taskManager.getTaskByTaskId(1);
        TaskManager testFileBackedTaskManager;
        try {
            testFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmpFile);
        } catch (IOException exception) {
            throw new ManagerSaveException("Error reading from file.", exception);
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
            throw new ManagerSaveException("Error reading from file.", exception);
        }
        assertEquals(1, testLoadFileBackedTaskManager.getAllTasks().size());
        assertEquals(1, testLoadFileBackedTaskManager.getAllEpics().size());
        assertEquals(1, testLoadFileBackedTaskManager.getAllSubTasks().size());
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
        assertEquals(0, testLoadFileBackedTaskManager.getAllTasks().size());
        assertEquals(0, testLoadFileBackedTaskManager.getAllEpics().size());
        assertEquals(0, testLoadFileBackedTaskManager.getAllSubTasks().size());
        assertEquals(0, testLoadFileBackedTaskManager.getHistory().size());
    }
}
