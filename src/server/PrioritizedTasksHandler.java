package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class PrioritizedTasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    Gson gson;

    public PrioritizedTasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] strings = path.split("/");

        if (exchange.getRequestMethod().equals("GET") && strings.length == 2) {
            String prioritizedTasks = gson.toJson(taskManager.getPrioritizedTasks());
            OutputStream os = exchange.getResponseBody();
            exchange.sendResponseHeaders(200, prioritizedTasks.length());
            os.write(prioritizedTasks.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }
}
