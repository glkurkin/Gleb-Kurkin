import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskTest {

    @Test
    public void testTaskEquality() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(15));
        Task task2 = new Task(1, "Задача 2", "Описание задачи 2", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(15));
        Assertions.assertEquals(task1, task2);
    }

    @Test
    public void testSubtaskEquality() {

        Duration duration = Duration.ofMinutes(60);
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(15);

        Subtask subtask1 = new Subtask(1, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, 1, duration, startTime);
        Subtask subtask2 = new Subtask(1, "Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, 1, duration, startTime);
        Assertions.assertEquals(subtask1, subtask2);
    }
}