package hw4.model;

import hw4.model.enums.TaskStatus;
import hw4.model.enums.TaskType;

import java.util.Objects;

public class Task {
    protected int taskId;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TaskType type;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = TaskType.TASK;
    }

    public Task(int taskId, String name, String description) {
        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = TaskType.TASK;
    }

    public Task(int taskId, String name, String description, TaskStatus status) {
        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String taskDescription) {
        this.description = taskDescription;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskType getType() {
        return type;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "ID=" + taskId +
                ", Type='" + type + '\'' +
                ", Name='" + name + '\'' +
                ", Description='" + description + '\'' +
                ", status='" + status + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}
