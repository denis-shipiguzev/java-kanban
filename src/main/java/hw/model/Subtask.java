package main.java.hw.model;

import main.java.hw.model.enums.TaskStatus;
import main.java.hw.model.enums.TaskType;

public class Subtask extends Task {
    private final int parentTaskId;

    public Subtask(String name, String description, int parentTaskId) {
        super(name, description);
        this.status = TaskStatus.NEW;
        this.type = TaskType.SUBTASK;
        this.parentTaskId = parentTaskId;
    }

    public Subtask(int taskId, String name, String description, int parentTaskId) {
        super(name, description);
        this.taskId = taskId;
        this.status = TaskStatus.NEW;
        this.type = TaskType.SUBTASK;
        this.parentTaskId = parentTaskId;
    }

    public Subtask(int taskId, String name, String description, int parentTaskId, TaskStatus status) {
        super(name, description);
        this.taskId = taskId;
        this.status = status;
        this.type = TaskType.SUBTASK;
        this.parentTaskId = parentTaskId;
    }

    public int getParentTaskId() {
        return parentTaskId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "ID=" + taskId +
                ", Type='" + type + '\'' +
                ", Name='" + name + '\'' +
                ", Description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", EpicId=" + parentTaskId + '}';
    }
}