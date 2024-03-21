import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.Task;
import main.java.hw.managers.Managers;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        /*
        1. Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
        */
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Task 1", "Test task 1",
                LocalDateTime.of(2024, 3, 5, 17, 0, 0), Duration.ofMinutes(30));
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Test task 2",
                LocalDateTime.of(2024, 3, 5, 17, 15, 0), Duration.ofMinutes(25));
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Epic 1", "Test epic 1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Test subtask 1", 3,
                LocalDateTime.of(2024, 3, 5, 15, 0, 0), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Test subtask 2", 3,
                LocalDateTime.of(2024, 3, 5, 15, 15, 0), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask2);
        Epic epic2 = new Epic("Epic 2", "Test epic 2");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask 3", "Test subtask 3", 6,
                LocalDateTime.of(2024, 3, 5, 20, 0, 0), Duration.ofMinutes(30));
        taskManager.createSubtask(subtask3);
        Subtask subtask4 = new Subtask("Subtask 4", "Test subtask 4", 6,
                LocalDateTime.of(2024, 3, 5, 20, 15, 0), Duration.ofMinutes(75));
        taskManager.createSubtask(subtask4);
        /*
        2. Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
         */
        System.out.println("---");
        System.out.println(taskManager.getTasks());
        System.out.println("---");
        System.out.println(taskManager.getEpics());
        System.out.println("---");
        System.out.println(taskManager.getSubtasks());
        System.out.println("---");
        System.out.println(taskManager.getTaskById(2));
        /*
        3. Измените статусы созданных объектов, распечатайте их.
        Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        */
        System.out.println("---");
//        task1 = new Task(task1.getTaskId(), "Task 1", "Test task 1", TaskStatus.IN_PROGRESS,
//                LocalDateTime.of(2024, 3, 7, 17, 0, 0), Duration.ofMinutes(30));
//        taskManager.updateTask(task1);
        System.out.println(taskManager.getTaskById(1));
        subtask1 = new Subtask(subtask1.getTaskId(), "Subtask 1", "Test subtask 1",
                3, TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, 3, 8, 15, 0, 0), Duration.ofMinutes(30));
        taskManager.updateSubtask(subtask1);
        System.out.println("---");
        System.out.println(taskManager.getEpicById(3));
        System.out.println("---");
        System.out.println(taskManager.getSubtaskById(4));
        System.out.println(taskManager.getSubtaskById(7));
        subtask1 = new Subtask(subtask1.getTaskId(), "Subtask 1", "Test subtask 1", 3,
                TaskStatus.DONE, LocalDateTime.of(2024, 3, 9, 15, 0, 0), Duration.ofMinutes(30));
        taskManager.updateSubtask(subtask1);
        subtask2 = new Subtask(subtask2.getTaskId(), "Subtask 2", "Test subtask 2", 3,
                TaskStatus.DONE, LocalDateTime.of(2024, 3, 6, 18, 0, 0), Duration.ofMinutes(30));
        taskManager.updateSubtask(subtask2);
        System.out.println("---");
        System.out.println(taskManager.getEpicById(3));
        /*
        4. И, наконец, попробуйте удалить одну из задач и один из эпиков.
        */
        System.out.println("---");
        taskManager.deleteTaskById(2);
        System.out.println(taskManager.getTasks());
        System.out.println("---");
        System.out.println(taskManager.getEpicSubtasks(3));
        taskManager.deleteSubtaskById(4);
        taskManager.deleteEpicById(3);
        System.out.println(taskManager.getEpics());
        System.out.println("---");
        System.out.println(taskManager.getHistory());
        System.out.println("---");
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : taskManager.getEpics()) {
            System.out.println(epic);
            for (Task task : taskManager.getEpicSubtasks(epic.getTaskId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
