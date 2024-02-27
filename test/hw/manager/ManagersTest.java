package hw.manager;

import main.java.hw.managers.Managers;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.managers.historymanagers.HistoryManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    /*
    убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    */
    @Test
    void shouldInitializeTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager not initialized.");
    }

    @Test
    void shouldInitializeHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager not initialized.");
    }
}