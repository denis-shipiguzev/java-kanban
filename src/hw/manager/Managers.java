package hw.manager;

import hw.manager.taskmanagers.TaskManager;
import hw.manager.historymanagers.HistoryManager;
import hw.manager.taskmanagers.InMemoryTaskManager;
import hw.manager.historymanagers.InMemoryHistoryManager;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
