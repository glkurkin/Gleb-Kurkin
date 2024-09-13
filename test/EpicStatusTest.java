import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicStatusTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testEpicStatusAllNew() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");
        taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId(), Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(10));
        taskManager.createSubtask("Подзадача 2", "Описание подзадачи 2", epic.getId(), Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(120));

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void testEpicStatusAllDone() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");

        Subtask subtask1 = taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId(), Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(10));
        Subtask subtask2 = taskManager.createSubtask("Подзадача 2", "Описание подзадачи 2", epic.getId(), Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(120));

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);

        taskManager.updateEpicStatus(epic.getId());

        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }


    @Test
    public void testEpicStatusNewAndDone() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");

        Subtask subtask1 = taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId(), Duration.ofMinutes(60), LocalDateTime.now());
        Subtask subtask2 = taskManager.createSubtask("Подзадача 2", "Описание подзадачи 2", epic.getId(), Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(60));

        subtask1.setStatus(TaskStatus.DONE);

        taskManager.updateEpicStatus(epic.getId());

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicStatus(epic.getId()));
    }



    @Test
    public void testEpicStatusInProgress() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");

        Subtask subtask1 = taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId(), Duration.ofMinutes(60), LocalDateTime.now());
        Subtask subtask2 = taskManager.createSubtask("Подзадача 2", "Описание подзадачи 2", epic.getId(), Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(60));


        subtask1.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateEpicStatus(epic.getId());

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicStatus(epic.getId()));
    }



    @Test
    public void testCreateUniqueSubtasksWithoutOverlap() {
        Epic epic = taskManager.createEpic("Эпик 1", "Описание эпика 1");

        LocalDateTime start1 = LocalDateTime.now().plusMinutes(10);
        LocalDateTime start2 = LocalDateTime.now().plusMinutes(120);

        Subtask subtask1 = taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", epic.getId(), Duration.ofMinutes(60), start1);
        Subtask subtask2 = taskManager.createSubtask("Подзадача 2", "Описание подзадачи 2", epic.getId(), Duration.ofMinutes(60), start2);

        Assertions.assertNotNull(subtask1);
        Assertions.assertNotNull(subtask2);
        Assertions.assertEquals(TaskStatus.NEW, subtask1.getStatus());
        Assertions.assertEquals(TaskStatus.NEW, subtask2.getStatus());
    }

}