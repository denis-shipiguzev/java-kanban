import java.util.ArrayList;
import java.util.Arrays;
public class Epic extends Task{
    ArrayList<Integer> childId;
    public Epic(int taskId, String name, String description) {
        super(taskId, name, description);
        this.status = TaskStatus.NEW;
        this.type = TaskType.EPIC;
        this.childId = new ArrayList<Integer>();
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
