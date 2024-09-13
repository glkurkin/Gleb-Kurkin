import managers.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utils.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager = Managers.getDefault();

    @Test
    public void testCreateAndRetrieveTask() {
        Task task = taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.now());
        Assertions.assertNotNull(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask);
    }

    @Test
    public void testCreateAndRetrieveEpic() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");
        Assertions.assertNotNull(epic);
        Task retrievedEpic = taskManager.getTaskById(epic.getId());
        assertEquals(epic, retrievedEpic);
    }

    @Test
    public void testCreateAndRetrieveSubtask() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");
        Subtask subtask = taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(90));
        Assertions.assertNotNull(subtask);
        Task retrievedSubtask = taskManager.getTaskById(subtask.getId());
        Assertions.assertEquals(subtask, retrievedSubtask);
    }



    @Test
    public void testTaskHistory() {
        LocalDateTime startTime1 = LocalDateTime.now().plusMinutes(15);
        LocalDateTime startTime2 = startTime1.plusMinutes(90);

        Task task1 = taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(60), startTime1);
        Task task2 = taskManager.createTask("Задача 2", "Описание задачи 2", Duration.ofMinutes(60), startTime2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }


    @Test
    public void testDeleteTask() {
        Task task = taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.deleteTask(task.getId());
        Assertions.assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    public void testGetHistory() {
        Task task1 = taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(10));
        Task task2 = taskManager.createTask("Задача 2", "Описание задачи 2", Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(50));

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }


}
