package hw4.service.TaskManagers;

import hw4.model.Epic;
import hw4.model.Subtask;
import hw4.model.Task;
import hw4.model.enums.TaskStatus;

import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {

    private int taskId = 0;
    // 1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int generateTaskId() {
        return ++taskId;
    }

    // 2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // 2.a Получение списка всех задач.
    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    // 2.b  Удаление всех задач.
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (Subtask subtask : subtasks.values()) {
            int parentId = subtask.getParentTaskId();
            int taskId = subtask.getTaskId();
            epics.get(parentId).removeSubtaskId(taskId);
            setStatusEpic(parentId);
        }
        subtasks.clear();
    }

    // 2.c  Получение по идентификатору.
    @Override
    public Task getTaskByTaskId(int taskId) {
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicByTaskId(int taskId) {
        return epics.get(taskId);
    }

    @Override
    public Subtask getSubTaskByTaskId(int taskId) {
        return subtasks.get(taskId);
    }

    // 2.d  Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public void addTask(Task task) {
        tasks.put(generateTaskId(), task);
        task.setTaskId(taskId);
    }

    @Override
    public void addEpic(Epic epic) {
        epics.put(generateTaskId(), epic);
        epic.setTaskId(taskId);
    }

    @Override
    public void addSubTask(Subtask subtask) {
        int parentId = subtask.getParentTaskId();
        boolean hasParentTaskId = epics.containsKey(parentId);
        if (hasParentTaskId) {
            subtasks.put(generateTaskId(), subtask);
            epics.get(parentId).addSubtaskId(taskId);
            subtask.setTaskId(taskId);
            setStatusEpic(parentId);
        }
    }

    // 2.e Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        ArrayList<Integer> ids = epic.getSubTaskIds();
        boolean hasEqualsTaskId = false;
        for (int id : ids) {
            if (id == epic.getTaskId()) {
                hasEqualsTaskId = true;
            }
        }
        if (!hasEqualsTaskId) {
            tasks.put(epic.getTaskId(), epic);
            setStatusEpic(epic.getTaskId());
        }
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        int parentId = subtask.getParentTaskId();
        boolean hasParentTaskId = epics.containsKey(parentId);
        if (hasParentTaskId) {
            subtasks.put(subtask.getTaskId(), subtask);
            setStatusEpic(parentId);
        }
    }

    // 2.f Удаление по идентификатору.
    @Override
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void removeSubTaskById(int taskId) {
        int parentId = subtasks.get(taskId).getParentTaskId();
        boolean hasParentTaskId = epics.containsKey(parentId);
        if (hasParentTaskId) {
            epics.get(parentId).removeSubtaskId(taskId);
            setStatusEpic(parentId);
        }
        subtasks.remove(taskId);
    }

    @Override
    public void removeEpicById(int taskId) {
        ArrayList<Integer> ids = epics.remove(taskId).getSubTaskIds();
        for (int id : ids) {
            subtasks.remove(id);
        }
    }

    // 3.a  Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<Subtask> getSubTasksOfEpics(int taskId) {
        ArrayList<Subtask> list = new ArrayList<>();
        Epic epic = epics.get(taskId);
        for (Integer taskid : epic.getSubTaskIds()) {
            list.add(subtasks.get(taskid));
        }
        return list;
    }

    private void setStatusEpic(int parentId) {
        boolean hasStatusNew = true;
        boolean hasStatusDone = true;
        for (Integer taskId : epics.get(parentId).getSubTaskIds()) {
            Subtask subtask = subtasks.get(taskId);
            if (subtask.getStatus() != TaskStatus.NEW) {
                hasStatusNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                hasStatusDone = false;
            }
            if (hasStatusNew) {
                epics.get(parentId).setStatus(TaskStatus.NEW);
            } else if (hasStatusDone) {
                epics.get(parentId).setStatus(TaskStatus.DONE);
            } else {
                epics.get(parentId).setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}


