import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private static final String FILE_HEADER = "id,type,name,status,description,epic\n";

    protected void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write(FILE_HEADER);
            for (Task task : getAllTasks()) {
                writer.write(taskToString(task));
            }
        } catch (IOException e) {
            throw new FileSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String taskToString(Task task) {
        TaskType type = task instanceof Epic ? TaskType.EPIC :
                task instanceof Subtask ? TaskType.SUBTASK :
                        TaskType.TASK;
        return String.format("%d,%s,%s,%s,%s,%s\n",
                task.getId(),
                type,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : ""
        );
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                    manager.updateEpicStatus(((Subtask) task).getEpicId());
                } else {
                    manager.tasks.put(task.getId(), task);
                }
            }
        } catch (IOException e) {
            throw new FileLoadException("Ошибка загрузки из файла", e);
        }
        return manager;
    }


    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, duration, startTime);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(id, name, description, status, duration, startTime, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    @Override
    public Task createTask(String name, String description) {
        Task task = super.createTask(name, description);
        save();
        return task;
    }

    @Override
    public Epic createEpic(String name, String description) {
        Epic epic = super.createEpic(name, description);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(String name, String description, int epicId) {
        Subtask subtask = super.createSubtask(name, description, epicId);
        save();
        return subtask;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public Task updateTask(int taskId, String name, String description) {
        Task updatedTask = super.updateTask(taskId, name, description);
        save();
        return updatedTask;
    }

    @Override
    public boolean deleteTask(int taskId) {
        boolean deleted = super.deleteTask(taskId);
        save();
        return deleted;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }
}
