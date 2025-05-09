import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private Path tempFile;

    @BeforeEach
    public void setUp() throws Exception {
        tempFile = Files.createTempFile("taskManager", ".txt");
        taskManager = FileBackedTaskManager.loadFromFile(tempFile.toFile());
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testSaveAndLoadEmptyTasks() {
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void testSaveAndLoadAfterDeletion() {
        taskManager.createTask("Задача 1", "Описание задачи 1");
        assertEquals(1, taskManager.getAllTasks().size());

        taskManager.save();
        taskManager = FileBackedTaskManager.loadFromFile(tempFile.toFile());

        taskManager.deleteTask(1);
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void testSaveAndLoadTasks() {
        taskManager.createTask("Задача 1", "Описание задачи 1");
        taskManager.createTask("Задача 2", "Описание задачи 2");
        assertEquals(2, taskManager.getAllTasks().size());

        taskManager.save();
        taskManager = FileBackedTaskManager.loadFromFile(tempFile.toFile());

        assertEquals(2, taskManager.getAllTasks().size());
    }
}
