package main.java.hw.servers.adapters;

import com.google.gson.*;
import main.java.hw.model.Epic;
import main.java.hw.model.Subtask;
import main.java.hw.model.Task;
import main.java.hw.model.enums.TaskStatus;
import main.java.hw.model.enums.TaskType;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskAdapter implements JsonDeserializer<Task> {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (type.getTypeName().equals(Subtask.class.getTypeName())) {
            Subtask task = gson.fromJson(jsonElement, Subtask.class);
            if (jsonObject.has("status")) {
                TaskStatus status = jsonDeserializationContext.deserialize(jsonObject.get("status"), TaskStatus.class);
                task.setStatus(status);
            } else {
                task.setStatus(TaskStatus.NEW);
            }
            task.setType(TaskType.SUBTASK);
            return task;
        } else if (type.getTypeName().equals(Epic.class.getTypeName())) {
            Epic task = gson.fromJson(jsonElement, Epic.class);
            task.setType(TaskType.EPIC);
            return task;
        } else {
            Task task = gson.fromJson(jsonElement, Task.class);
            if (jsonObject.has("status")) {
                TaskStatus status = jsonDeserializationContext.deserialize(jsonObject.get("status"), TaskStatus.class);
                task.setStatus(status);
            } else {
                task.setStatus(TaskStatus.NEW);
            }
            task.setType(TaskType.TASK);
            return task;
        }
    }
}
