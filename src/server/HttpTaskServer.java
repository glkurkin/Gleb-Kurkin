package server;

import com.sun.net.httpserver.HttpServer;
import handlers.*;
import managers.InMemoryTaskManager;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;

    public HttpTaskServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        TaskManager taskManager = new InMemoryTaskManager();

        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedTasksHandler(taskManager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP сервер остановлен");
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }
}
