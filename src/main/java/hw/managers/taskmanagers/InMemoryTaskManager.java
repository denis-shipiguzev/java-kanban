package main.java.hw.managers.taskmanagers;

import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.Task;
import main.java.hw.model.enums.TaskStatus;
import main.java.hw.managers.Managers;
import main.java.hw.managers.historymanagers.HistoryManager;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private int taskId = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    // 1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    private int generateTaskId() {
        return ++taskId;
    }

    // 2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // 2.a Получение списка всех задач.
    @Override
    public ArrayList<Task> getAllTasks() {
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
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicByTaskId(int taskId) {
        historyManager.add(epics.get(taskId));
        return epics.get(taskId);
    }

    @Override
    public Subtask getSubTaskByTaskId(int taskId) {
        historyManager.add(subtasks.get(taskId));
        return subtasks.get(taskId);
    }

    // 2.d  Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public int addTask(Task task) {
        tasks.put(generateTaskId(), task);
        task.setTaskId(taskId);
        return task.getTaskId();
    }

    @Override
    public int addEpic(Epic epic) {
        List<Integer> ids = epic.getSubTaskIds();
        boolean hasEqualsTaskId = false;
        for (int id : ids) {
            if (id == epic.getTaskId()) {
                hasEqualsTaskId = true;
                break;
            }
        }
        if (!hasEqualsTaskId) {
            epics.put(generateTaskId(), epic);
            epic.setTaskId(taskId);
        }
        return epic.getTaskId();
    }

    @Override
    public int addSubTask(Subtask subtask) {
        int parentId = subtask.getParentTaskId();
        boolean hasParentTaskId = epics.containsKey(parentId);
        if (hasParentTaskId) {
            subtasks.put(generateTaskId(), subtask);
            epics.get(parentId).addSubtaskId(taskId);
            subtask.setTaskId(taskId);
            setStatusEpic(parentId);
        }
        return subtask.getTaskId();
    }

    // 2.e Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public int updateTask(Task task) {
        tasks.put(task.getTaskId(), task);
        return task.getTaskId();
    }

    @Override
    public int updateEpic(Epic epic) {
        List<Integer> ids = epic.getSubTaskIds();
        boolean hasEqualsTaskId = false;
        for (int id : ids) {
            if (id == epic.getTaskId()) {
                hasEqualsTaskId = true;
                break;
            }
        }
        if (!hasEqualsTaskId) {
            epics.put(epic.getTaskId(), epic);
            setStatusEpic(epic.getTaskId());
        }
        return epic.getTaskId();
    }

    @Override
    public int updateSubTask(Subtask subtask) {
        int parentId = subtask.getParentTaskId();
        boolean hasParentTaskId = epics.containsKey(parentId);
        if (hasParentTaskId) {
            subtasks.put(subtask.getTaskId(), subtask);
            setStatusEpic(parentId);
        }
        return subtask.getTaskId();
    }

    // 2.f Удаление по идентификатору.
    @Override
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
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
        historyManager.remove(taskId);
    }

    @Override
    public void removeEpicById(int taskId) {
        List<Integer> ids = epics.remove(taskId).getSubTaskIds();
        historyManager.remove(taskId);
        for (int id : ids) {
            subtasks.remove(id);
        }
    }

    // 3.a  Получение списка всех подзадач определённого эпика.

    public List<Subtask> getEpicSubtasks(int taskId) {
        return epics.get(taskId).getSubTaskIds()
                .stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}


