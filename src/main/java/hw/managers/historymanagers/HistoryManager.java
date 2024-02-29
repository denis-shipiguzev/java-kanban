package main.java.hw.managers.historymanagers;

import main.java.hw.model.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
