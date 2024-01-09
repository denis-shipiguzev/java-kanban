package hw4.service;

import hw4.model.Epic;
import hw4.model.Subtask;
import hw4.model.Task;
import hw4.model.TaskStatus;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {

    public int taskId = 1;
    // 1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int generateTaskId(){
        return taskId++;
    }

    public int getTaskId(){
        return taskId;
    }
    // 2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // 2.a Получение списка всех задач.
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics(){
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubTasks(){
        return new ArrayList<>(subtasks.values());
    }
    // 2.b  Удаление всех задач.
    public void removeAllTasks() {
        tasks.clear();
    }
    public void removeAllEpics() {
        subtasks.clear();
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
        boolean hasParentTaskId = epics.containsKey(parentId);
        if (hasParentTaskId) {
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
        boolean hasParentTaskId = epics.containsKey(parentId);
        if (hasParentTaskId) {
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
        boolean hasParentTaskId = epics.containsKey(parentId);
        if (hasParentTaskId) {
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
    // 3.a  Получение списка всех подзадач определённого эпика.
    public ArrayList<Subtask> getSubTasksOfEpics(int taskId) {
        ArrayList<Subtask> list = new ArrayList<>();
        Epic epic = epics.get(taskId);
        for (Integer taskid: epic.getSubTaskIds()) {
            list.add(subtasks.get(taskid));
        }
        return list;
    }
}


