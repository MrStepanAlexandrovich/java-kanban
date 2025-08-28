package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager = HttpTaskServer.getTaskManager();
    private Gson gson = HttpTaskServer.getGson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] strings = path.split("/");

        if (exchange.getRequestMethod().equals("GET")) {         //GET-запросы
            if (strings[1].equals("tasks") && strings.length == 2) {
                List<Task> tasks = taskManager.getTasks();
                String tasksJson = gson.toJson(tasks);

                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(200, tasksJson.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(tasksJson.getBytes());
                }
            } else if (strings[1].equals("tasks") && strings.length == 3) {
                int id;

                try {
                    id = Integer.parseInt(strings[2]);
                } catch (NumberFormatException e) {
                    sendNotFound(exchange, "Incorrect task ID!");
                    return;
                }

                Task task = taskManager.getTask(id);
                if (task != null) {
                    String taskJson = gson.toJson(task);

                    try (OutputStream os = exchange.getResponseBody()) {
                        exchange.sendResponseHeaders(200, taskJson.length());
                        os.write(taskJson.getBytes());
                    }
                } else {
                    sendNotFound(exchange, "Task with ID = " + id + " was not found!");
                }
            }
        } else if (exchange.getRequestMethod().equals("POST")) {
            
        }
    }
}