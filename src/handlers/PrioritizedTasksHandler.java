package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedTasksHandler implements HttpHandler {
    private final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGetPrioritizedTasks(exchange);
        } else {
            sendResponse(exchange, "Метод не поддерживается", 405);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        List<Task> prioritizedTasks = (List<Task>) taskManager.getPrioritizedTasks();
        String response = gson.toJson(prioritizedTasks);
        sendResponse(exchange, response, 200);
    }

    private void sendResponse(HttpExchange exchange, String responseText, int statusCode) throws IOException {
        byte[] response = responseText.getBytes();
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
