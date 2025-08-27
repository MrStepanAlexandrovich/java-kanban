package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Handler;

public class HttpTaskServer {
    public TaskManager taskManager;
    Gson gson;

    {
        taskManager = Managers.getDefault();
    }

    public static void main(String[] args) {
        HttpServer httpServer;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

     class GetTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Task> tasks = taskManager.getTasks();

            exchange.sendResponseHeaders(200, 0);
            exchange.getRequestBody()
        }
    }
}
