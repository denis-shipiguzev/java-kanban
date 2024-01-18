package hw4.service.HistoryManagers;

import hw4.model.Task;
import hw4.service.Managers;
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
    void shouldEqualsTaskAndHistoryTask(){
        Task task = new Task("Test addTask 1", "Test addTask description 1");
        historyManager.add(task);
        Task historyTask = historyManager.getHistory().get(0);
        assertEquals(task, historyTask, "Tasks are not equal.");
    }
}