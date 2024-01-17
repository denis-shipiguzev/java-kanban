package hw4.service.HistoryManagers;

import java.util.List;

import hw4.model.Task;

public interface HistoryManager {
    void add(Task task); // Добавить

    List<Task> getHistory(); // Получить
}
