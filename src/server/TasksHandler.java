package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
                    sendNotFound(exchange, "Incorrect task ID!", 404);
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
                    sendNotFound(exchange, "Task with ID = " + id + " was not found!", 404);
                }
            }
        } else if (exchange.getRequestMethod().equals("POST") && strings.length == 2     //POST-запросы
                && strings[1].equals("tasks")) {
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(requestBody);

            if (jsonElement.isJsonObject()) {
                Task task = gson.fromJson(requestBody, Task.class);
                boolean taskManagerNotContains = taskManager.getTasks()
                        .stream()
                        .noneMatch(task1 -> task1.getId() == task.getId());
                if (!taskManager.findIntersection(task)) {
                    if (taskManagerNotContains || task.getId() == null) {
                        taskManager.addTask(task);
                        sendText(exchange, "Task has been added!", 201);
                    } else {
                        taskManager.updateTask(task.getId(), task);
                        sendText(exchange, "Task has been updated!", 201);
                    }
                } else {
                    sendHasInteractions(exchange, "Interaction was found! Task wasn't added!", 406);
                }
            }
        } else if (exchange.getRequestMethod().equals("DELETE") && strings.length == 3 && strings[1].equals("tasks")) {
            int id;

            try {
                id = Integer.parseInt(strings[2]);
            } catch (NumberFormatException e) {
                sendNotFound(exchange, "Incorrect task ID!", 404);
                return;
            }

            Task task = taskManager.getTask(id);

            if (task != null) {
                taskManager.removeTask(task.getId());
                sendText(exchange,"Task with ID = " + id + " was removed!", 200);
            } else {
                sendText(exchange, "Task with ID = " + id + " wasn't found!", 404);
            }
        }
    }
}