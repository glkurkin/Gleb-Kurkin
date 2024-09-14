package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler implements HttpHandler {
    private final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetSubtasks(exchange);
                break;
            case "POST":
                handlePostSubtask(exchange);
                break;
            case "DELETE":
                handleDeleteSubtasks(exchange);
                break;
            default:
                sendResponse(exchange, "Метод не поддерживается", 405);
                break;
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = (List<Subtask>) taskManager.getSubtasks();
        String response = gson.toJson(subtasks);
        sendResponse(exchange, response, 200);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        Subtask subtask = gson.fromJson(body, Subtask.class);
        taskManager.createSubtask(subtask);
        sendResponse(exchange, gson.toJson(subtask), 201);
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteSubtasks();
        sendResponse(exchange, "", 200);
    }

    private void sendResponse(HttpExchange exchange, String responseText, int statusCode) throws IOException {
        byte[] response = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
