import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @Test
    public void testCreateAndRetrieveTask() {
        Task task = taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.now());
        assertNotNull(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask);
    }

    @Test
    public void testCreateEpicAndSubtask() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");
        Subtask subtask = taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        assertEquals(epic.getId(), subtask.getEpicId());
    }

    @Test
    public void testTaskHistory() {
        Task task = taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.getTaskById(task.getId());
        List<Task> history = taskManager.getHistory();
        assertTrue(history.contains(task));
    }

    @Test
    public void testDeleteTask() {
        Task task = taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.deleteTask(task.getId());
        assertNull(taskManager.getTaskById(task.getId()));
    }

}
