public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        /*
1. Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
2. Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
3. Измените статусы созданных объектов, распечатайте их.
   Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
4. И, наконец, попробуйте удалить одну из задач и один из эпиков.
         */

        TaskManager tm = new TaskManager();
        Task task1 = new Task(tm.getTaskId(), "Task 1","Test task 1");
        tm.addTask(task1);
        Task task2 = new Task(tm.getTaskId(),"Task 2","Test task 2");
        tm.addTask(task2);
        Epic epic1 = new Epic(tm.getTaskId(),"Epic 1", "Test epic 1");
        tm.addEpic(epic1);
        Subtask subtask1 = new Subtask(tm.getTaskId(),"Subtask 1", "Test subtask 1", 3);
        tm.addSubTask(subtask1);
        Subtask subtask2 = new Subtask(tm.getTaskId(),"Subtask 2", "Test subtask 2", 3);
        tm.addSubTask(subtask2);
        Epic epic2 = new Epic(tm.getTaskId(),"Epic 2", "Test epic 2");
        tm.addEpic(epic2);
        Subtask subtask3 = new Subtask(tm.getTaskId(),"Subtask 3", "Test subtask 3", 6);
        tm.addSubTask(subtask3);
        System.out.println("---");
        System.out.println(tm.listAllTask());
        System.out.println("---");
        System.out.println(tm.listAllEpics());
        System.out.println("---");
        System.out.println(tm.listAllSubTasks());
        System.out.println("---");
    }
}
