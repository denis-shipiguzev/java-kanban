package main.java.hw.managers.taskmanagers;

import main.java.hw.exceptions.ManagerSaveException;
import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.Task;
import main.java.hw.model.enums.TaskType;
import main.java.hw.managers.CSVTaskFormatter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;
    private static final String HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    @Override
    public int addTask(Task task) {
        super.addTask(task);
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
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public int addSubTask(Subtask subtask) {
        super.addSubTask(subtask);
        save();
        return subtask.getTaskId();
    }

    @Override
    public int updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
        return subtask.getTaskId();
    }

    @Override
    public void removeSubTaskById(int taskId) {
        super.removeSubTaskById(taskId);
        save();
    }

    @Override
    public int addEpic(Epic epic) {
        super.addEpic(epic);
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
    public void removeEpicById(int taskId) {
        super.removeEpicById(taskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), StandardCharsets.UTF_8))) {
            writer.write(HEADER);
            writer.newLine();
            for (Task task : tasks.values()) {
                writer.write(CSVTaskFormatter.toString(task));
                writer.newLine();
            }
            for (Epic epic : epics.values()) {
                writer.write(CSVTaskFormatter.toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(CSVTaskFormatter.toString(subtask));
                writer.newLine();
            }
            writer.newLine();
            writer.write(CSVTaskFormatter.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Error writing to file.", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.toPath());
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<String> lines = new java.util.ArrayList<>(reader.lines().toList());
            boolean hasHEADER = lines.get(0).trim().equals(HEADER);
            if (hasHEADER) {
                lines.remove(lines.get(0));
            }
            if (lines.isEmpty()) {
                return fileBackedTaskManager;
            }

            int lastString = lines.size() - 1;
            String stringHistory = lines.get(lastString);
            List<Integer> history = CSVTaskFormatter.historyFromString(stringHistory);
            if (!history.isEmpty()) {
                lines.remove(lastString);
            }

            for (String line : lines) {
                if (!line.isBlank() && !line.equals("\n")) {
                    Task task = CSVTaskFormatter.fromString(line);
                    TaskType type = CSVTaskFormatter.fromString(line).getType();
                    switch (type) {
                        case EPIC -> epics.put(task.getTaskId(), (Epic) task);
                        case SUBTASK -> subtasks.put(task.getTaskId(), (Subtask) task);
                        case TASK -> tasks.put(task.getTaskId(), task);
                    }
                }
            }
            for (Integer id : history) {
                if (tasks.containsKey(id)) {
                    historyManager.add(tasks.get(id));
                } else if (subtasks.containsKey(id)) {
                    historyManager.add(subtasks.get(id));
                } else if (epics.containsKey(id)) {
                    historyManager.add(epics.get(id));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file.");
        }
        return fileBackedTaskManager;
    }
}
