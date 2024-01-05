public class Subtask extends Task{
    private int parentTaskId;

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
        String result = "Task{" +
                "ID=" + taskId +
                ", Type='" + type + '\'' +
                ", Name='" + name + '\'' +
                ", Description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", EpicId=" + parentTaskId + '}';
        return result;
    }
}
