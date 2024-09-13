package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TaskManager {
    Task createTask(String name, String description, Duration duration, LocalDateTime now);

    Epic createEpic(String name, String description);

    Subtask createSubtask(String name, String description, int epicId, Duration duration, LocalDateTime localDateTime);

    Task updateTask(int id, String name, String description, TaskStatus done);

    boolean deleteTask(int id);

    Task getTaskById(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    void deleteAllTasks();

    List<Subtask> getSubtasksOfEpic(int epicId);

    List<Task> getHistory();

    void updateEpicStatus(int epicId);


    TaskStatus getEpicStatus(int id);

    Map<Integer, Task> getTasks();
}
