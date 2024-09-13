package server;

import com.sun.net.httpserver.HttpServer;
import handlers.TaskHandler;
import managers.InMemoryTaskManager;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;

    public HttpTaskServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public void start() {
        server.start();
        System.out.println("HTTP сервер запущен на порту " + PORT);
        TaskManager taskManager = new InMemoryTaskManager();
        server.createContext("/tasks", new TaskHandler(taskManager));
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
