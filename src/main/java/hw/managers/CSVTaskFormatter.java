package main.java.hw.managers;

import main.java.hw.managers.historymanagers.HistoryManager;
import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.Task;
import main.java.hw.model.enums.TaskStatus;
import main.java.hw.model.enums.TaskType;

import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormatter {
    private static final String DELIMITER = ",";

    public static String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getTaskId()).append(DELIMITER);
        sb.append(task.getType()).append(DELIMITER);
        sb.append(task.getName()).append(DELIMITER);
        sb.append(task.getStatus()).append(DELIMITER);
        sb.append(task.getDescription()).append(DELIMITER);
        if (task.getType() == TaskType.SUBTASK) {
            sb.append(((Subtask) task).getParentTaskId());
        }
        return sb.toString();
    }

    public static Task fromString(String value) {
        String[] values = value.split(DELIMITER);
        int epicId = 0;
        int taskId = Integer.parseInt(values[0]);
        TaskType type = TaskType.valueOf(values[1]);
        String name = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];
        if (type.equals(TaskType.SUBTASK)) {
            epicId = Integer.parseInt(values[5]);
        }
        switch (type) {
            case EPIC -> {
                return new Epic(taskId, name, description, status);
            }
            case SUBTASK -> {
                return new Subtask(taskId, name, description, epicId, status);
            }
            default -> {
                return new Task(taskId, name, description, status);
            }
        }
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value.isBlank() || value.isEmpty()) {
            return history;
        }
        String[] values = value.split(DELIMITER);
        for (String task : values) {
            history.add(Integer.parseInt(task));
        }
        return history;
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.getTaskId()).append(DELIMITER);
        }
        if (!sb.toString().isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

}
