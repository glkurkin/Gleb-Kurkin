import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalDateTime;

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

    public static FileBackedTaskManager loadFromFile(File file) throws FileLoadException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                try {
                    Task task = fromString(line);

                    if (task instanceof Epic) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        manager.subtasks.put(task.getId(), (Subtask) task);
                        manager.updateEpicStatus(((Subtask) task).getEpicId());
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Ошибка разбора строки: " + line + ". Пропуск задачи.");

                }
            }
        } catch (IOException e) {
            throw new FileLoadException("Ошибка загрузки из файла: " + file.getName(), e);
        }

        return manager;
    }



    private static Task fromString(String value) {
        String[] parts = value.split(",");

        TaskType type = TaskType.valueOf(parts[1]);

        switch (type) {
            case TASK:
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Неправильный формат данных для Task. Ожидалось как минимум 5 полей, но получено: " + parts.length);
                }
                int taskId = Integer.parseInt(parts[0]);
                String taskName = parts[2];
                TaskStatus taskStatus = TaskStatus.valueOf(parts[3]);
                String taskDescription = parts[4];
                Duration taskDuration = (parts.length > 5 && !parts[5].isEmpty())
                        ? Duration.ofMinutes(Long.parseLong(parts[5]))
                        : Duration.ZERO;
                LocalDateTime taskStartTime = (parts.length > 6 && !parts[6].isEmpty())
                        ? LocalDateTime.parse(parts[6])
                        : LocalDateTime.now();
                return new Task(taskId, taskName, taskDescription, taskStatus, taskDuration, taskStartTime);

            case EPIC:
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Неправильный формат данных для Epic. Ожидалось 5 полей, но получено: " + parts.length);
                }
                int epicId = Integer.parseInt(parts[0]);
                String epicName = parts[2];
                String epicDescription = parts[4];
                return new Epic(epicId, epicName, epicDescription);

            case SUBTASK:
                if (parts.length < 8) {
                    throw new IllegalArgumentException("Неправильный формат данных для Subtask. Ожидалось 8 полей, но получено: " + parts.length);
                }
                int subtaskId = Integer.parseInt(parts[0]);
                String subtaskName = parts[2];
                TaskStatus subtaskStatus = TaskStatus.valueOf(parts[3]);
                String subtaskDescription = parts[4];
                Duration subtaskDuration = (parts.length > 5 && !parts[5].isEmpty())
                        ? Duration.ofMinutes(Long.parseLong(parts[5]))
                        : Duration.ZERO;
                LocalDateTime subtaskStartTime = (parts.length > 6 && !parts[6].isEmpty())
                        ? LocalDateTime.parse(parts[6])
                        : LocalDateTime.now();
                int subtaskEpicId = Integer.parseInt(parts[7]);
                return new Subtask(subtaskId, subtaskName, subtaskDescription, subtaskStatus, subtaskEpicId, subtaskDuration, subtaskStartTime);

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }


    @Override
    public Task createTask(String name, String description, Duration duration, LocalDateTime now) {
        Task task = super.createTask(name, description, duration, now);
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
    public Subtask createSubtask(String name, String description, int epicId, Duration duration, LocalDateTime localDateTime) {
        Subtask subtask = super.createSubtask(name, description, epicId, duration, localDateTime);
        save();
        return subtask;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public Task updateTask(int taskId, String name, String description, TaskStatus done) {
        Task updatedTask = super.updateTask(taskId, name, description, done);
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