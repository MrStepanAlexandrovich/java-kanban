package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SubtasksHandler implements HttpHandler {
    private TaskManager taskManager = HttpTaskServer.getTaskManager();
    private Gson gson = HttpTaskServer.getGson();
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] strings = path.split("/");

        if (exchange.getRequestMethod().equals("GET") && strings[1].equals("subtasks") && strings.length == 2) {
            List<Subtask> subtasks = taskManager.getSubtasks();
            String subtasksJson = gson.toJson(subtasks);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(200, subtasksJson.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(subtasksJson.getBytes());
            }
        }
    }
}