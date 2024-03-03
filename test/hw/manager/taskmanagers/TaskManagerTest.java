package hw.manager.taskmanagers;

import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskByTaskId(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    /*
    проверьте, что экземпляры класса Task равны друг другу, если равен их id;
     */
    @Test
    void shouldTaskSameTask() {
        Task task = new Task("Task", "Test task 1");
        final int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskByTaskId(taskId);
        assertEquals(task, savedTask, "Tasks are not equal.");
    }

    /*
    проверьте, что наследники класса Task равны друг другу, если равен их id;
     */
    @Test
    void shouldEpicSameEpic() {
        Epic epic = new Epic("Epic", "Test epic 1");
        final int epicId = taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpicByTaskId(epicId);
        assertEquals(epic, savedEpic, "Epics are not equal.");
    }

    @Test
    void shouldSubtaskSameSubtask() {
        Epic epic = new Epic("Epic", "Test epic 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Test subtask 1", epic.getTaskId());
        final int subtaskId = taskManager.addSubTask(subtask);
        final Subtask savedSubtask = taskManager.getSubTaskByTaskId(subtaskId);
        assertEquals(subtask, savedSubtask, "Subtasks are not equal.");
    }

    /*
    проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    */
    @Test
    void shouldEpicNotSameSubtask() {
        ArrayList<Integer> childId = new ArrayList<>(List.of(1));
        Epic epic = new Epic(1, "Epic 1", "Test epic 1", childId);
        taskManager.addEpic(epic);
        assertNotEquals(epic, taskManager.getEpicByTaskId(1), "Epic created.");
    }

    /*
    проверьте, что объект Subtask нельзя сделать своим же эпиком;
    */
    @Test
    void shouldSubtaskNotSameEpic() {
        Subtask subtask = new Subtask(1, "Subtask 1", "Test subtask 1", 1);
        taskManager.addSubTask(subtask);
        assertNotEquals(subtask, taskManager.getSubTaskByTaskId(1), "Subtask created.");
    }

    /*
    проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
     */
    @Test
    void shouldAddTaskAndFind() {
        Task task = new Task("Task 1", "Test task 1");
        final int taskId = taskManager.addTask(task);
        Epic epic = new Epic("Epic 1", "Test epic 1");
        final int epicId = taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Test subtask 1", epic.getTaskId());
        final int subtaskId = taskManager.addSubTask(subtask);
        assertEquals(taskId, task.getTaskId(), "Invalid identifier.");
        assertEquals(epicId, epic.getTaskId(), "Invalid identifier.");
        assertEquals(subtaskId, subtask.getTaskId(), "Invalid identifier.");
    }

    @Test
    void shouldRemoveSubtaskFromEpicWhenDeleted() {
        Epic epic = new Epic("Epic 1", "Test epic 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Test subtask 1", epic.getTaskId());
        taskManager.addSubTask(subtask);
        taskManager.removeSubTaskById(subtask.getTaskId());
        final List<Subtask> subtasks = taskManager.getEpicSubtasks(epic.getTaskId());
        assertEquals(0, subtasks.size(), "subtasks of epic is not empty.");
    }

    @Test
    void shouldSetDescription() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addTask(task);
        task.setDescription("Test edit NewTask description");
        final Task savedTask = taskManager.getTaskByTaskId(taskId);
        assertEquals(task, savedTask, "Cannot set task description.");
    }
}
