import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    public static int taskId = 1;
    // 1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer,Subtask> subtasks = new HashMap<>();

    private int generateTaskId(){
        return taskId++;
    }

    public int getTaskId(){
        return taskId;
    }
    // 2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // 2.a Получение списка всех задач.
    public HashMap<Integer, Task> listAllTask(){
        return new HashMap<>(tasks);
    }

    public HashMap<Integer, Task> listAllEpics(){
        return new HashMap<>(epics);
    }

    public HashMap<Integer, Task> listAllSubTasks(){
        return new HashMap<>(subtasks);
    }
    // 2.b  Удаление всех задач.
    public void removeAllTasks() {
        tasks.clear();
    }
    public void removeAllEpics() {
        epics.clear();
    }
    public void removeAllSubTasks() {
        subtasks.clear();
    }

    // 2.d  Создание. Сам объект должен передаваться в качестве параметра.
    public void addTask(Task task) {
        tasks.put(generateTaskId(), task);
    }
    public void addEpic(Epic epic) {
        epics.put(generateTaskId(),epic);
    }
    public void addSubTask(Subtask subtask){
        int parentId = subtask.getParentTaskId();
        boolean parentTaskIds = epics.containsKey(parentId);

        if (parentTaskIds) {
            subtasks.put(generateTaskId(), subtask);
        }
    }



    public void updateTask(Task task){
        tasks.put(task.getTaskId(), task);
    }

}


