package hw.manager.taskmanagers;

import main.java.hw.managers.taskmanagers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void createTaskManager() {
        taskManager = new InMemoryTaskManager();
    }
}