import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class EpicStatusTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testEpicStatusAllNew() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");
        taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        taskManager.createSubtask("Подзадача 2", "Описание подзадачи 2", epic.getId());

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void testEpicStatusAllDone() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        Subtask subtask2 = taskManager.createSubtask("Подзадача 2", "Описание подзадачи 2", epic.getId());

        taskManager.updateTask(subtask1.getId(), "Подзадача 1", "Описание подзадачи 1", TaskStatus.DONE);
        taskManager.updateTask(subtask2.getId(), "Подзадача 2", "Описание подзадачи 2", TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void testEpicStatusNewAndDone() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        Subtask subtask2 = taskManager.createSubtask("Подзадача 2", "Описание подзадачи 2", epic.getId());

        taskManager.updateTask(subtask1.getId(), "Подзадача 1", "Описание подзадачи 1", TaskStatus.DONE);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testEpicStatusInProgress() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");
        Subtask subtask1 = taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId());

        taskManager.updateTask(subtask1.getId(), "Подзадача 1", "Описание подзадачи 1", TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}