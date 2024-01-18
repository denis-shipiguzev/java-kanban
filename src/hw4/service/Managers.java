package hw4.service;

import hw4.service.TaskManagers.TaskManager;
import hw4.service.HistoryManagers.HistoryManager;
import hw4.service.TaskManagers.InMemoryTaskManager;
import hw4.service.HistoryManagers.InMemoryHistoryManager;

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
