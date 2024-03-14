package main.java.hw.managers.taskmanagers;

import main.java.hw.exceptions.ManagerSaveException;
import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.Task;
import main.java.hw.model.enums.TaskType;
import main.java.hw.managers.utils.CSVTaskFormatter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;
    private static final String HEADER = "id,type,name,status,description,epic,startTime,duration,endTime";
    private static final CSVTaskFormatter csvTaskFormatter = new CSVTaskFormatter();

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    @Override
    public int createTask(Task task) {
        super.createTask(task);
        save();
        return task.getTaskId();
    }

    @Override
    public int updateTask(Task task) {
        super.updateTask(task);
        save();
        return task.getTaskId();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask.getTaskId();
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
        return subtask.getTaskId();
    }

    @Override
    public void deleteSubtaskById(int taskId) {
        super.deleteSubtaskById(taskId);
        save();
    }

    @Override
    public int createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic.getTaskId();
    }

    @Override
    public int updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic.getTaskId();
    }

    @Override
    public void deleteEpicById(int taskId) {
        super.deleteEpicById(taskId);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        super.getTaskById(taskId);
        save();
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(int taskId) {
        super.getEpicById(taskId);
        save();
        return epics.get(taskId);
    }

    @Override
    public Subtask getSubtaskById(int taskId) {
        super.getSubtaskById(taskId);
        save();
        return subtasks.get(taskId);
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), StandardCharsets.UTF_8))) {
            writer.write(HEADER);
            writer.newLine();
            for (Task task : tasks.values()) {
                writer.write(csvTaskFormatter.toString(task));
                writer.newLine();
            }
            for (Epic epic : epics.values()) {
                writer.write(csvTaskFormatter.toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(csvTaskFormatter.toString(subtask));
                writer.newLine();
            }
            writer.newLine();
            writer.write(csvTaskFormatter.historyToString(historyManager));
        } catch (IOException exception) {
            throw new ManagerSaveException("Error writing to file.", exception);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.toPath());
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<String> lines = new ArrayList<>(reader.lines().toList());
            if (lines.isEmpty()) {
                return fileBackedTaskManager;
            }
            boolean hasHEADER = lines.getFirst().trim().equals(HEADER);
            if (hasHEADER) {
                lines.removeFirst();
            }

            int lastString = lines.size() - 1;
            String stringHistory = lines.get(lastString);
            List<Integer> history = csvTaskFormatter.historyFromString(stringHistory);
            if (!history.isEmpty()) {
                lines.remove(lastString);
            }

            for (String line : lines) {
                if (!line.isBlank() && !line.equals("\n")) {
                    Task task = csvTaskFormatter.fromString(line);
                    TaskType type = csvTaskFormatter.fromString(line).getType();
                    switch (type) {
                        case EPIC -> fileBackedTaskManager.epics.put(task.getTaskId(), (Epic) task);
                        case SUBTASK -> fileBackedTaskManager.subtasks.put(task.getTaskId(), (Subtask) task);
                        case TASK -> fileBackedTaskManager.tasks.put(task.getTaskId(), task);
                    }
                }
            }
            for (Integer id : history) {
                if (fileBackedTaskManager.tasks.containsKey(id)) {
                    fileBackedTaskManager.historyManager.add(fileBackedTaskManager.tasks.get(id));
                } else if (fileBackedTaskManager.subtasks.containsKey(id)) {
                    fileBackedTaskManager.historyManager.add(fileBackedTaskManager.subtasks.get(id));
                } else if (fileBackedTaskManager.epics.containsKey(id)) {
                    fileBackedTaskManager.historyManager.add(fileBackedTaskManager.epics.get(id));
                }
            }
        } catch (IOException exception) {
            System.out.println("Error reading from file.");
        }
        return fileBackedTaskManager;
    }
}
