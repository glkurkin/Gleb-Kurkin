import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static TaskManager taskManager = Managers.getDefault();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        runTests();

        while (true) {
            printMenu();
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    createTask();
                    break;
                case 2:
                    createEpic();
                    break;
                case 3:
                    createSubtask();
                    break;
                case 4:
                    printAllTasks();
                    break;
                case 5:
                    printAllEpics();
                    break;
                case 6:
                    printSubtasksOfEpic();
                    break;
                case 7:
                    updateTask();
                    break;
                case 8:
                    deleteTaskById();
                    break;
                case 9:
                    deleteAllTasks();
                    break;
                case 10:
                    printHistory();
                    break;
                case 0:
                    System.out.println("Выход из программы.");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nМеню:");
        System.out.println("1. Создать задачу");
        System.out.println("2. Создать эпик");
        System.out.println("3. Создать подзадачу");
        System.out.println("4. Получить список всех задач");
        System.out.println("5. Получить список всех эпиков");
        System.out.println("6. Получить список всех подзадач определённого эпика");
        System.out.println("7. Обновить задачу");
        System.out.println("8. Удалить задачу по идентификатору");
        System.out.println("9. Удалить все задачи");
        System.out.println("10. Показать историю просмотров задач");
        System.out.println("0. Выход");
        System.out.print("Введите ваш выбор: ");
    }

    private static void createTask() {
        System.out.print("Введите название задачи: ");
        String name = scanner.nextLine();
        System.out.print("Введите описание задачи: ");
        String description = scanner.nextLine();
        Task task = taskManager.createTask(name, description, Duration.ofMinutes(30), LocalDateTime.now());
        System.out.println("Задача создана: " + task.getId());
    }

    private static void createEpic() {
        System.out.print("Введите название эпика: ");
        String name = scanner.nextLine();
        System.out.print("Введите описание эпика: ");
        String description = scanner.nextLine();
        Epic epic = taskManager.createEpic(name, description);
        System.out.println("Эпик создан: " + epic.getId());
    }

    private static void createSubtask() {
        System.out.print("Введите идентификатор эпика: ");
        int epicId = Integer.parseInt(scanner.nextLine());
        System.out.print("Введите название подзадачи: ");
        String name = scanner.nextLine();
        System.out.print("Введите описание подзадачи: ");
        String description = scanner.nextLine();
        Subtask subtask = taskManager.createSubtask(name, description, epicId);
        System.out.println("Подзадача создана: " + subtask.getId());
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
    }

    private static void printAllEpics() {
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
        }
    }

    private static void printSubtasksOfEpic() {
        System.out.print("Введите идентификатор эпика: ");
        int epicId = Integer.parseInt(scanner.nextLine());
        System.out.println("Подзадачи эпика:");
        for (Subtask subtask : taskManager.getSubtasksOfEpic(epicId)) {
            System.out.println(subtask);
        }
    }

    private static void updateTask() {
        System.out.print("Введите идентификатор задачи: ");
        int id = Integer.parseInt(scanner.nextLine());
        Task task = taskManager.getTaskById(id);
        if (task == null) {
            System.out.println("Задача не найдена.");
            return;
        }
        System.out.print("Введите новое название задачи: ");
        String name = scanner.nextLine();
        System.out.print("Введите новое описание задачи: ");
        String description = scanner.nextLine();
        taskManager.updateTask(id, name, description, TaskStatus.DONE);
        System.out.println("Задача обновлена.");
    }

    private static void deleteTaskById() {
        System.out.print("Введите идентификатор задачи: ");
        int id = Integer.parseInt(scanner.nextLine());
        if (taskManager.deleteTask(id)) {
            System.out.println("Задача удалена.");
        } else {
            System.out.println("Задача не найдена.");
        }
    }

    private static void deleteAllTasks() {
        taskManager.deleteAllTasks();
        System.out.println("Все задачи удалены.");
    }

    private static void printHistory() {
        List<Task> history = taskManager.getHistory();
        if (history.isEmpty()) {
            System.out.println("История просмотров пуста.");
        } else {
            System.out.println("История просмотров задач:");
            for (Task task : history) {
                System.out.println(task);
            }
        }
    }

    private static void runTests() {
        System.out.println("Запуск теста...");

        taskManager.createTask("Задача 1", "Описание задачи 1", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask("Задача 2", "Описание задачи 2", Duration.ofMinutes(30), LocalDateTime.now());

        taskManager.createEpic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic("Эпик 2", "Описание эпика 2");

        taskManager.createSubtask("Подзадача 1", "Описание подзадачи 1", 1);
        taskManager.createSubtask("Подзадача 2", "Описание подзадачи 2", 1);
        taskManager.createSubtask("Подзадача 3", "Описание подзадачи 3", 2);

        System.out.println("Все задачи:");
        printAllTasks();

        System.out.println("\nВсе эпики:");
        printAllEpics();

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);

        System.out.println("\nИстория после просмотра задач 1, 2, 3:");
        printHistory();

        taskManager.updateTask(1, "Задача 1 обновлена", "Описание задачи 1 обновлено", TaskStatus.DONE);

        taskManager.updateTask(3, "Подзадача 1 обновлена", "Описание подзадачи 1 обновлено", TaskStatus.DONE);
        taskManager.updateTask(4, "Подзадача 2 обновлена", "Описание подзадачи 2 обновлено", TaskStatus.DONE);

        System.out.println("\nЭпики и их статусы после обновления подзадач:");
        printAllEpics();

        taskManager.deleteTask(2);
        taskManager.deleteTask(1);

        System.out.println("\nОставшиеся задачи после удаления:");
        printAllTasks();

        System.out.println("Тест завершен.");
    }
}
