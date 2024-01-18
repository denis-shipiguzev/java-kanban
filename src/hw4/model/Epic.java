package hw4.model;

import hw4.model.enums.TaskStatus;
import hw4.model.enums.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> childId;

    public Epic(String name, String description) {
        super(name, description);
        this.status = TaskStatus.NEW;
        this.type = TaskType.EPIC;
        this.childId = new ArrayList<>();
    }

    public Epic(int taskId, String name, String description) {
        super(taskId, name, description);
        this.type = TaskType.EPIC;
        this.childId = new ArrayList<>();
    }

    public Epic(int taskId, String name, String description, ArrayList<Integer> childId) {
        super(taskId, name, description);
        this.type = TaskType.EPIC;
        this.childId = childId;
    }

    public List<Integer> getSubTaskIds() {
        return new ArrayList<>(childId);
    }

    public void addSubtaskId(int id) {
        childId.add(id);
    }

    public void removeSubtaskId(int id) {
        childId.remove((Integer) id);
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "ID=" + taskId +
                ", Type='" + type + '\'' +
                ", Name='" + name + '\'' +
                ", Description='" + description + '\'' +
                ", status='" + status + '\'' + '}';
        if (!childId.isEmpty()) {
            result = result + ", subtasks=" + childId;
        }
        return result;
    }
}
