import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(taskToString(task));
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения в файл", e);
        }
    }

    private String taskToString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s\n",
                task.getId(),
                task.getClass().getSimpleName(),
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
            throw new RuntimeException("Ошибка загрузки из файла", e);
        }
        return manager;
    }


    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];
        switch (type) {
            case "Task":
                return new Task(id, name, description, status);
            case "Epic":
                return new Epic(id, name, description);
            case "Subtask":
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public Task updateTask(int id, String name, String description) {
        Task updatedTask = super.updateTask(id, name, description);
        save();
        return updatedTask;
    }

    @Override
    public boolean deleteTask(int id) {
        boolean deleted = super.deleteTask(id);
        save();
        return deleted;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }
}