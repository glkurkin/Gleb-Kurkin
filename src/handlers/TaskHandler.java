package handlers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tasks.Task;
import managers.TaskManager;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetTasks(exchange);
                break;
            case "POST":
                break;
            case "DELETE":
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        Map<Integer, Task> tasks = taskManager.getTasks();
        String response = gson.toJson(tasks);
        sendText(exchange, response);
    }
}
