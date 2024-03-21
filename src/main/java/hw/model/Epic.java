package main.java.hw.model;

import main.java.hw.model.enums.TaskStatus;
import main.java.hw.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> childId = new ArrayList<>();
    private final TaskType type = TaskType.EPIC;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.status = TaskStatus.NEW;
    }

    public Epic(int taskId, String name, String description) {
        super(taskId, name, description);
    }
    public Epic(int taskId, String name, String description, List<Integer> childId) {
        super(taskId, name, description);
        this.childId = childId;
    }

    public Epic(int taskId, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(taskId, name, description);
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"ID\":").append(taskId).append(",");
        sb.append("\"Type\":\"").append(type).append("\",");
        sb.append("\"Name\":\"").append(name).append("\",");
        sb.append("\"Description\":\"").append(description).append("\",");
        sb.append("\"Status\":\"").append(status).append("\",");
        sb.append("\"StartTime\":\"").append(startTime).append("\",");
        sb.append("\"Duration\":\"").append(duration).append("\"");
        if (endTime != null) {
            sb.append(",\"EndTime\":\"").append(endTime).append("\"");
        }
        if (childId != null && !childId.isEmpty()) {
            sb.append(",\"SubTaskIds\":").append(childId);
        }
        sb.append("}");
        return sb.toString();
    }
}
