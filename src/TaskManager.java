import java.util.HashMap;
import java.util.ArrayList;

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

    // 2.c  Получение по идентификатору.
    public Task getTaskByTaskId(int taskId){
        return tasks.get(taskId);
    }
    public Epic getEpicByTaskId(int taskId){
        return epics.get(taskId);
    }
    public Subtask getSubTaskByTaskId(int taskId){
        return subtasks.get(taskId);
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
        boolean parentTaskId = epics.containsKey(parentId);
        if (parentTaskId) {
            epics.get(parentId).addSubtaskId(getTaskId());
            subtasks.put(generateTaskId(), subtask);
        }
    }
    // 2.e Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task){
        tasks.put(task.getTaskId(), task);
    }
    public void updateSubTask(Subtask subtask){
        int parentId = subtask.getParentTaskId();
        boolean parentTaskId = epics.containsKey(parentId);
        if (parentTaskId) {
            subtasks.put(subtask.getTaskId(), subtask);
            epics.get(parentId).setStatus(subtask.getStatus());
            int countSubTask = epics.get(parentId).childId.size();
            for (int i = 0;  i < countSubTask; i++) {
                int count = 0;
                if (subtask.getStatus().equals(TaskStatus.DONE)) {
                    count++;
                }
                if (count == countSubTask){
                    epics.get(parentId).setStatus(TaskStatus.DONE);
                }
            }
        }
    }

    // 2.f Удаление по идентификатору.
    public Task removeTaskById(int taskId){
        return tasks.remove(taskId);
    }
    public Subtask removeSubTaskById(int taskId){
        int parentId = subtasks.get(taskId).getParentTaskId();
        boolean parentTaskId = epics.containsKey(parentId);
        if (parentTaskId) {
            epics.get(parentId).removeSubtaskId(taskId);
        }
        return subtasks.remove(taskId);
    }
    public Epic removeEpicById(int taskId){
        ArrayList<Integer> ids= epics.get(taskId).getSubTaskIds();
        for (int id : ids) {
            subtasks.remove(id);
        }
        return epics.remove(taskId);
    }
}


