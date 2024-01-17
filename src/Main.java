import hw4.model.Epic;
import hw4.model.Subtask;
import hw4.model.Task;
import hw4.model.enums.TaskStatus;
import hw4.service.Managers;
import hw4.service.TaskManagers.TaskManager;

public class Main {

    public static void main(String[] args) {
        /*
        1. Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
        */
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Task 1", "Test task 1");
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Test task 2");
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Epic 1", "Test epic 1");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Test subtask 1", 3);
        taskManager.addSubTask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Test subtask 2", 3);
        taskManager.addSubTask(subtask2);
        Epic epic2 = new Epic("Epic 2", "Test epic 2");
        taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask 3", "Test subtask 3", 6);
        taskManager.addSubTask(subtask3);
        /*
        2. Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
         */
        System.out.println("---");
        System.out.println(taskManager.getAllTasks());
        System.out.println("---");
        System.out.println(taskManager.getAllEpics());
        System.out.println("---");
        System.out.println(taskManager.getAllSubTasks());
        System.out.println("---");
        System.out.println(taskManager.getTaskByTaskId(2));
        /*
        3. Измените статусы созданных объектов, распечатайте их.
        Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        */
        System.out.println("---");
        task1 = new Task(task1.getTaskId(), "Task 1", "Test task 1", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);
        System.out.println(taskManager.getTaskByTaskId(1));
        subtask1 = new Subtask(subtask1.getTaskId(), "Subtask 1", "Test subtask 1", 3, TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subtask1);
        System.out.println("---");
        System.out.println(taskManager.getEpicByTaskId(3));
        System.out.println("---");
        System.out.println(taskManager.getSubTaskByTaskId(4));
        subtask1 = new Subtask(subtask1.getTaskId(), "Subtask 1", "Test subtask 1", 3, TaskStatus.DONE);
        taskManager.updateSubTask(subtask1);
        subtask2 = new Subtask(subtask2.getTaskId(), "Subtask 2", "Test subtask 2", 3, TaskStatus.DONE);
        taskManager.updateSubTask(subtask2);
        System.out.println("---");
        System.out.println(taskManager.getEpicByTaskId(3));
        /*
        4. И, наконец, попробуйте удалить одну из задач и один из эпиков.
        */
        System.out.println("---");
        taskManager.removeTaskById(2);
        System.out.println(taskManager.getAllTasks());
        System.out.println("---");
        taskManager.getSubTasksOfEpics(3);
        taskManager.removeSubTaskById(4);
        taskManager.removeEpicById(3);
        System.out.println(taskManager.getAllEpics());
        System.out.println("---");
        System.out.println(taskManager.getHistory());
        System.out.println("---");
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : taskManager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : taskManager.getSubTasksOfEpics(epic.getTaskId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
