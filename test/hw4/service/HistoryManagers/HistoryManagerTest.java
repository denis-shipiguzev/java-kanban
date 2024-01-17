package hw4.service.HistoryManagers;

import hw4.model.Task;
import hw4.service.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    public void createHistoryManager() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Покупки", "Список покупок");
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}