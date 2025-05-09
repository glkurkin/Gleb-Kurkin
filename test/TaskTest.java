import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TaskTest {

    @Test
    public void testTaskEquality() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task(1, "Задача 2", "Описание задачи 2", TaskStatus.NEW);
        Assertions.assertEquals(task1, task2);
    }

    @Test
    public void testSubtaskEquality() {
        Subtask subtask1 = new Subtask(1, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask(1, "Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, 1);
        Assertions.assertEquals(subtask1, subtask2);
    }
}
