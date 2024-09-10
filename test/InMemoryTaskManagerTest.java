import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private TaskManager taskManager = Managers.getDefault();

    @Test
    public void testCreateAndRetrieveTask() {
        Task task = taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.now());
        Assertions.assertNotNull(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        Assertions.assertEquals(task, retrievedTask);
    }

    @Test
    public void testCreateAndRetrieveEpic() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");
        Assertions.assertNotNull(epic);
        Task retrievedEpic = taskManager.getTaskById(epic.getId());
        Assertions.assertEquals(epic, retrievedEpic);
    }

    @Test
    public void testCreateAndRetrieveSubtask() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");
        Subtask subtask = taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        Assertions.assertNotNull(subtask);
        Task retrievedSubtask = taskManager.getTaskById(subtask.getId());
        Assertions.assertEquals(subtask, retrievedSubtask);
    }

    @Test
    public void testTaskHistory() {
        Task task1 = taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = taskManager.createTask("Задача 2", "Описание задачи 2", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task1, history.get(0));
        Assertions.assertEquals(task2, history.get(1));
    }

    @Test
    public void testDeleteTask() {
        Task task = taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.deleteTask(task.getId());
        Assertions.assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    public void testGetHistory() {
        Task task1 = taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = taskManager.createTask("Задача 2", "Описание задачи 2", Duration.ofMinutes(30), LocalDateTime.now());

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(task1, history.get(0));
        Assertions.assertEquals(task2, history.get(1));
    }

}
