import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Epic> epics;
    protected Map<Integer, Subtask> subtasks;
    private int id;
    private HistoryManager historyManager;
    private TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.id = 1;
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(new TaskStartTimeComparator());
    }

    private boolean isOverlapping(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }

    @Override
    public Task createTask(String name, String description, Duration duration, LocalDateTime startTime) {
        int id = this.id++;
        Task task = new Task(id, name, description, TaskStatus.NEW, duration, startTime);

        for (Task existingTask : prioritizedTasks) {
            if (isOverlapping(task, existingTask)) {
                throw new IllegalArgumentException("Задачи пересекаются по времени выполнения");
            }
        }

        tasks.put(id, task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(String name, String description) {
        int id = this.id++;
        Epic epic = new Epic(id, name, description);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(String name, String description, int epicId, Duration duration, LocalDateTime startTime) {
        int id = this.id++;
        Subtask subtask = new Subtask(id, name, description, TaskStatus.NEW, epicId, duration, startTime);

        for (Task existingTask : prioritizedTasks) {
            if (isOverlapping(subtask, existingTask)) {
                throw new IllegalArgumentException("Задачи пересекаются по времени выполнения");
            }
        }

        subtasks.put(id, subtask);
        prioritizedTasks.add(subtask);

        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id " + epicId + " не найден.");
        }
        epic.addSubtask(id);
        epic.updateStatus(getSubtasksOfEpic(epicId));
        epic.updateTiming(getSubtasksOfEpic(epicId));
        updateEpicStatus(epicId);

        return subtask;
    }



    @Override
    public Task updateTask(int id, String name, String description, TaskStatus done) {
        Task task = getTaskById(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            task.setName(name);
            task.setDescription(description);
            if (task instanceof Epic) {
                updateEpicStatus(id);
            } else if (task instanceof Subtask) {
                updateEpicStatus(((Subtask) task).getEpicId());
            }

            for (Task existingTask : prioritizedTasks) {
                if (isOverlapping(task, existingTask)) {
                    throw new IllegalArgumentException("Задачи пересекаются по времени выполнения");
                }
            }

            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public boolean deleteTask(int id) {
        Task task = getTaskById(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            tasks.remove(id);

            if (task instanceof Subtask) {
                Subtask subtask = (Subtask) task;
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.removeSubtask(id);
                    updateEpicStatus(epic.getId());
                }
            }

            return true;
        }
        return false;
    }


    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            task = epics.get(id);
        }
        if (task == null) {
            task = subtasks.get(id);
        }
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }



    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        List<Subtask> subtaskList = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    subtaskList.add(subtask);
                }
            }
        }
        return subtaskList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null || epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);

            if (subtask != null) {
                if (subtask.getStatus() != TaskStatus.NEW) {
                    allNew = false;
                }
                if (subtask.getStatus() != TaskStatus.DONE) {
                    allDone = false;
                }
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (!allNew) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public TaskStatus getEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null || epic.getSubtaskIds().isEmpty()) {
            return TaskStatus.NEW;
        }

        boolean allNew = true;
        boolean allDone = true;


        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);

            if (subtask == null) {
                continue;
            }

            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }

            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }

            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                return TaskStatus.IN_PROGRESS;
            }
        }

        if (allNew) {
            return TaskStatus.NEW;
        }

        if (allDone) {
            return TaskStatus.DONE;
        }

        return TaskStatus.IN_PROGRESS;
    }



    public void addTask(Task task) {
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    public void printAllTasks() {
        List<Task> tasks = getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("Задач нет.");
        } else {
            for (Task task : tasks) {
                System.out.println(task.getId() + ": " + task.getName() + " [" + task.getStatus() + "]");
            }
        }
    }

    public void printAllEpics() {
        List<Epic> epics = getAllEpics();
        if (epics.isEmpty()) {
            System.out.println("Эпиков нет.");
        } else {
            for (Epic epic : epics) {
                System.out.println(epic.getId() + ": " + epic.getName() + " [" + epic.getStatus() + "]");
            }
        }
    }

    public void printSubtasksOfEpic(int epicId) {
        List<Subtask> subtasks = getSubtasksOfEpic(epicId);
        if (subtasks.isEmpty()) {
            System.out.println("Подзадач нет.");
        } else {
            for (Subtask subtask : subtasks) {
                System.out.println(subtask.getId() + ": " + subtask.getName() + " [" + subtask.getStatus() + "]");
            }
        }
    }
}