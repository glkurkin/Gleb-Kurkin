import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, Subtask> subtasks;
    private int id;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.id = 1;
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public Task createTask(String name, String description) {
        int id = this.id++;
        Task task = new Task(id, name, description, TaskStatus.NEW);
        tasks.put(id, task);
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
    public Subtask createSubtask(String name, String description, int epicId) {
        int id = this.id++;
        Subtask subtask = new Subtask(id, name, description, TaskStatus.NEW, epicId);
        subtasks.put(id, subtask);
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.addSubtask(id);
            updateEpicStatus(epicId);
        }
        return subtask;
    }

    @Override
    public Task updateTask(int id, String name, String description) {
        Task task = getTaskById(id);
        if (task != null) {
            task.setName(name);
            task.setDescription(description);
            if (task instanceof Epic) {
                updateEpicStatus(id);
            } else if (task instanceof Subtask) {
                updateEpicStatus(((Subtask) task).getEpicId());
            }
        }
        return task;
    }

    @Override
    public boolean deleteTask(int id) {
        if (tasks.remove(id) != null) {
            return true;
        }
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
            return true;
        }
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic parentEpic = epics.get(subtask.getEpicId());
            if (parentEpic != null) {
                parentEpic.removeSubtask(id);
                updateEpicStatus(parentEpic.getId());
            }
            return true;
        }
        return false;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        }
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        }
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
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

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.updateStatus(new ArrayList<>(subtasks.values()));
        }
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

