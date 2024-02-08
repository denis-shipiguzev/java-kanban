package hw.manager.historymanagers;

import java.util.List;

import hw.model.Task;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
