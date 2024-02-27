package hw.manager.historymanagers;

import main.java.hw.managers.historymanagers.HistoryManager;
import main.java.hw.model.Task;
import main.java.hw.managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    public void createHistoryManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        historyManager.add(new Task("Test addTask 1", "Test addTask description 1"));
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "History is not empty.");
        assertEquals(1, history.size(), "History is not empty.");
    }

    /*
    убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    */
    @Test
    void shouldEqualsTaskAndHistoryTask() {
        Task task = new Task("Test addTask 1", "Test addTask description 1");
        historyManager.add(task);
        Task historyTask = historyManager.getHistory().get(0);
        assertEquals(task, historyTask, "Tasks are not equal.");
    }

    @Test
    void shouldRemoveHistoryTask() {
        Task task = new Task("Test addTask 1", "Test addTask description 1");
        historyManager.add(task);
        historyManager.remove(task.getTaskId());
        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "History task not deleted.");
    }

    @Test
    void shouldRemoveDuplicateHistoryTask() {
        historyManager.add(new Task(1, "Test addTask 1", "Test addTask description 1"));
        historyManager.add(new Task(2, "Test addTask 2", "Test addTask description 2"));
        historyManager.add(new Task(1, "Test addTask 1", "Test addTask description 1"));
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Duplicate tasks in history found.");
    }
}