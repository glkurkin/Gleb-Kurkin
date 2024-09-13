package handlers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
                handlePostTask(exchange);
                break;
            case "DELETE":
                handleDeleteTasks(exchange);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        if (body.isEmpty()) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == 0) {
            taskManager.createTask(task);
            String response = gson.toJson(task);
            exchange.sendResponseHeaders(201, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            taskManager.updateTask(task);
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            taskManager.deleteTasks();
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } else {
            String[] params = query.split("=");
            if (params.length == 2 && params[0].equals("id")) {
                int id = Integer.parseInt(params[1]);
                boolean isDeleted = taskManager.deleteTask(id);
                if (isDeleted) {
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    exchange.sendResponseHeaders(404, 0);
                }
                exchange.close();
            } else {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
            }
        }
    }


    private void handleGetTasks(HttpExchange exchange) throws IOException {
        Map<Integer, Task> tasks = taskManager.getTasks();
        String response = gson.toJson(tasks);
        sendText(exchange, response);
    }
}