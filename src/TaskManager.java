import java.util.List;

public interface TaskManager {
    Task createTask(String name, String description);

    Epic createEpic(String name, String description);

    Subtask createSubtask(String name, String description, int epicId);

    Task updateTask(int id, String name, String description);

    boolean deleteTask(int id);

    Task getTaskById(int id);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    void deleteAllTasks();

    List<Subtask> getSubtasksOfEpic(int epicId);

    List<Task> getHistory();

}
