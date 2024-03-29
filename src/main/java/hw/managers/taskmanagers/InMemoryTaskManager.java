package main.java.hw.managers.taskmanagers;

import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.Task;
import main.java.hw.model.enums.TaskStatus;
import main.java.hw.managers.Managers;
import main.java.hw.managers.historymanagers.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private int taskId = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    // 1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime).thenComparing(Task::getEndTime);
    protected final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    private int getNextId() {
        return ++taskId;
    }

    // 2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // 2.a Получение списка всех задач.
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // 2.b  Удаление всех задач.
    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            int parentId = subtask.getParentTaskId();
            int taskId = subtask.getTaskId();
            epics.get(parentId).removeSubtaskId(taskId);
            setStatusEpic(parentId);
            setEndTimeEpic(parentId);
        }
        subtasks.clear();
    }

    // 2.c  Получение по идентификатору.
    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(int taskId) {
        historyManager.add(epics.get(taskId));
        return epics.get(taskId);
    }

    @Override
    public Subtask getSubtaskById(int taskId) {
        historyManager.add(subtasks.get(taskId));
        return subtasks.get(taskId);
    }

    // 2.d  Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public int createTask(Task task) {
        if (checkIntersectionTasks(task)) {
            task.setTaskId(getNextId());
            tasks.put(task.getTaskId(), task);
            prioritizedTasks.add(task);
        }
        return task.getTaskId();
    }

    @Override
    public int createEpic(Epic epic) {
        List<Integer> ids = epic.getSubTaskIds();
        boolean hasEqualsTaskId = false;
        for (int id : ids) {
            if (id == epic.getTaskId()) {
                hasEqualsTaskId = true;
                break;
            }
        }
        if (!hasEqualsTaskId) {
            epic.setTaskId(getNextId());
            epics.put(epic.getTaskId(), epic);
        }
        return epic.getTaskId();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int parentId = subtask.getParentTaskId();
        boolean hasParentTaskId = epics.containsKey(parentId);
        if (checkIntersectionTasks(subtask)) {
            if (hasParentTaskId) {
                subtask.setTaskId(getNextId());
                subtasks.put(subtask.getTaskId(), subtask);
                epics.get(parentId).addSubtaskId(taskId);
                setStatusEpic(parentId);
                setEndTimeEpic(parentId);
                prioritizedTasks.add(subtask);
            }
        }
        return subtask.getTaskId();
    }

    // 2.e Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public int updateTask(Task task) {
        prioritizedTasks.remove(tasks.get(task.getTaskId()));
        if (checkIntersectionTasks(task)) {
            tasks.put(task.getTaskId(), task);
            prioritizedTasks.add(task);
        }
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
            setEndTimeEpic(epic.getTaskId());
        }
        return epic.getTaskId();
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        int parentId = subtask.getParentTaskId();
        boolean hasParentTaskId = epics.containsKey(parentId);
        if (hasParentTaskId) {
            prioritizedTasks.remove(subtasks.get(subtask.getTaskId()));
            if (checkIntersectionTasks(subtask)) {
                subtasks.put(subtask.getTaskId(), subtask);
                setStatusEpic(parentId);
                setEndTimeEpic(parentId);
                prioritizedTasks.add(subtask);
            }
        }
        return subtask.getTaskId();
    }

    // 2.f Удаление по идентификатору.
    @Override
    public void deleteTaskById(int taskId) {
        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteSubtaskById(int taskId) {
        int parentId = subtasks.get(taskId).getParentTaskId();
        boolean hasParentTaskId = epics.containsKey(parentId);
        if (hasParentTaskId) {
            epics.get(parentId).removeSubtaskId(taskId);
            setStatusEpic(parentId);
            setEndTimeEpic(parentId);
            prioritizedTasks.remove(subtasks.get(taskId));
        }
        subtasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpicById(int taskId) {
        List<Integer> ids = epics.get(taskId).getSubTaskIds();
        historyManager.remove(taskId);
        for (int id : ids) {
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
        }
        epics.remove(taskId);
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

    private void setEndTimeEpic(int parentId) {
        List<Subtask> ids = getEpicSubtasks(parentId);
        if (!ids.isEmpty()) {
            LocalDateTime minStartDate = ids.stream()
                    .map(Subtask::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElseThrow(NoSuchElementException::new);
            LocalDateTime maxEndDate = ids.stream()
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .orElseThrow(NoSuchElementException::new);
            Duration duration = Duration.between(minStartDate, maxEndDate);
            epics.get(parentId).setStartTime(minStartDate);
            epics.get(parentId).setEndTime(maxEndDate);
            epics.get(parentId).setDuration(duration);
        }
    }

    private boolean checkIntersectionTasks(Task task) {
        getPrioritizedTasks().stream()
                .filter(t -> {
                    LocalDateTime startTime = t.getStartTime();
                    LocalDateTime endTime = t.getEndTime();
                    return (
                            task.getStartTime().equals(startTime) ||
                                    task.getEndTime().equals(endTime) ||
                                    (task.getStartTime().isAfter(startTime) && task.getEndTime().isBefore(endTime)) ||
                                    (task.getStartTime().isBefore(startTime) && task.getEndTime().isAfter(endTime)) ||
                                    (task.getStartTime().isBefore(startTime) && task.getEndTime().isAfter(startTime))
                    );

                })
                .findAny()
                .ifPresent(task1 -> {
                    throw new IllegalStateException("Задачи не должны пересекаться по времени выполнения");
                });

        return true;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }
}


