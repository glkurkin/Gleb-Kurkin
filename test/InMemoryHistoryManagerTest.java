import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    public void testAddTask() {
        Task task = new Task(1, "Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(15));
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task, history.get(0));
    }

    @Test
    public void testRemoveTask() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(15));
        Task task2 = new Task(2, "Задача 2", "Описание задачи 2", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(15));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task2, history.get(0));
    }

    @Test
    public void testGetHistory() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(15));
        Task task2 = new Task(2, "Задача 2", "Описание задачаи 2", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(15));
        Task task3 = new Task(3, "Задача 3", "Описание задачи 3", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(15));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(3, history.size());
        Assertions.assertEquals(task1, history.get(0));
        Assertions.assertEquals(task2, history.get(1));
        Assertions.assertEquals(task3, history.get(2));
    }

    @Test
    public void testAddDuplicateTask() {
        Task task1 = new Task(1, "Задача 1", "Описание задачи 1", TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(15));
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task1, history.get(0));
    }


}