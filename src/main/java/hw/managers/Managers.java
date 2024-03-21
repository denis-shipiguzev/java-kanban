package main.java.hw.managers;

import main.java.hw.managers.taskmanagers.InMemoryTaskManager;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.managers.historymanagers.HistoryManager;
import main.java.hw.managers.historymanagers.InMemoryHistoryManager;
import main.java.hw.managers.taskmanagers.FileBackedTaskManager;

import java.nio.file.Paths;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(Paths.get("src/main/resources/tasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
