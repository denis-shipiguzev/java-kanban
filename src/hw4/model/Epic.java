package hw4.model;

import java.util.ArrayList;
public class Epic extends Task {
    public ArrayList<Integer> childId;
    public Epic(int taskId, String name, String description) {
        super(taskId, name, description);
        this.status = TaskStatus.NEW;
        this.type = TaskType.EPIC;
        this.childId = new ArrayList<Integer>();
    }
/*
    public Epic(int taskId, String name, String description, TaskStatus status) {
        super(taskId, name, description);
        this.status = status;
        this.type = TaskType.EPIC;
        this.childId = new ArrayList<Integer>();
    }
*/
    public ArrayList<Integer> getSubTaskIds() {
        return new ArrayList<>(childId);
    }

    public void addSubtaskId(int id) {
        childId.add(id);
    }
    public void removeSubtaskId(int id) {
        childId.remove(id);
    }

    @Override
    public String toString() {
        String result = "Task{" +
                "ID=" + taskId +
                ", Type='" + type + '\'' +
                ", Name='" + name + '\'' +
                ", Description='" + description + '\'' +
                ", status='" + status + '\'' + '}';
        if(!childId.isEmpty()) {
            result = result + ", subtasks=" + childId;
        }
        return result;
    }
}
