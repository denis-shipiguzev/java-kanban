package hw4.service.HistoryManagers;

import hw4.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> tasksHistory = new LinkedList<>();
    private static final int HISTORY_MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (tasksHistory.size() < HISTORY_MAX_SIZE) {
            tasksHistory.add(task);
        } else {
            tasksHistory.removeFirst();
            tasksHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(tasksHistory);
    }
}
