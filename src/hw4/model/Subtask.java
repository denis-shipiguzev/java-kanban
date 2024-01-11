package hw4.model;

import hw4.model.enums.TaskStatus;
import hw4.model.enums.TaskType;

public class Subtask extends Task {
    private final int parentTaskId;

    public Subtask(int taskId, String name, String description, int parentTaskId) {
        super(taskId, name, description);
        this.status = TaskStatus.NEW;
        this.type = TaskType.SUBTUSK;
        this.parentTaskId = parentTaskId;
    }

    public Subtask(int taskId, String name, String description, int parentTaskId, TaskStatus status) {
        super(taskId, name, description);
        this.status = status;
        this.type = TaskType.SUBTUSK;
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
