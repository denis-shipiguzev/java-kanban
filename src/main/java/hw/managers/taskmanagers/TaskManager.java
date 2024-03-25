package main.java.hw.managers.taskmanagers;

import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {
    // 2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // 2.a Получение списка всех задач.
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    // 2.b  Удаление всех задач.
    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    // 2.c  Получение по идентификатору.
    Task getTaskById(int taskId);

    Epic getEpicById(int taskId);

    Subtask getSubtaskById(int taskId);

    // 2.d  Создание. Сам объект должен передаваться в качестве параметра.
    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask);

    // 2.e Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    int updateTask(Task task);

    int updateEpic(Epic epic);

    int updateSubtask(Subtask subtask);

    // 2.f Удаление по идентификатору.
    void deleteTaskById(int taskId);

    void deleteSubtaskById(int taskId);

    void deleteEpicById(int taskId);

    // 3.a  Получение списка всех подзадач определённого эпика.
    List<Subtask> getEpicSubtasks(int taskId);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

}
