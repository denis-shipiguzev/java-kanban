package hw4.service.TaskManagers;

import hw4.model.Epic;
import hw4.model.Subtask;
import hw4.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    // 2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // 2.a Получение списка всех задач.
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubTasks();

    // 2.b  Удаление всех задач.
    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubTasks();

    // 2.c  Получение по идентификатору.
    Task getTaskByTaskId(int taskId);

    Epic getEpicByTaskId(int taskId);

    Subtask getSubTaskByTaskId(int taskId);

    // 2.d  Создание. Сам объект должен передаваться в качестве параметра.
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(Subtask subtask);

    // 2.e Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(Subtask subtask);

    // 2.f Удаление по идентификатору.
    void removeTaskById(int taskId);

    void removeSubTaskById(int taskId);

    void removeEpicById(int taskId);

    // 3.a  Получение списка всех подзадач определённого эпика.
    ArrayList<Subtask> getSubTasksOfEpics(int taskId);

    List<Task> getHistory();
}
