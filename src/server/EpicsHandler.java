package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager = HttpTaskServer.getTaskManager();
    private Gson gson = HttpTaskServer.getGson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] strings = path.split("/");

        if (exchange.getRequestMethod().equals("GET")) {
            if (strings[1].equals("epics") && strings.length == 2) {
                List<Epic> epics = taskManager.getEpics();
                String epicsJson = gson.toJson(epics);
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(200, epicsJson.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(epicsJson.getBytes());
                }
            } else if (strings[1].equals("epics") && strings.length == 3) {
                int id;

                try {
                    id = Integer.parseInt(strings[2]);
                } catch (NumberFormatException e) {
                    sendNotFound(exchange, "Incorrect epic ID!", 404);
                    return;
                }

                Epic epic = taskManager.getEpic(id);
                if (epic != null) {
                    String epicJson = gson.toJson(epic);

                    try (OutputStream os = exchange.getResponseBody()) {
                        exchange.sendResponseHeaders(200, epicJson.length());
                        os.write(epicJson.getBytes());
                    }
                } else {
                    sendNotFound(exchange, "Epic with ID = " + id + " was not found!", 404);
                }
            } else if (strings[1].equals("epics") && strings.length == 4 && strings[3].equals("subtasks")) {
                int id;

                try {
                    id = Integer.parseInt(strings[2]);
                } catch (NumberFormatException e) {
                    sendNotFound(exchange, "Incorrect epic ID!", 404);
                    return;
                }

                Epic epic = taskManager.getEpic(id);
                if (epic != null) {
                    List<Subtask> subtasks = epic.getSubtasks();
                    String epicJson = gson.toJson(subtasks);

                    try (OutputStream os = exchange.getResponseBody()) {
                        exchange.sendResponseHeaders(200, epicJson.length());
                        os.write(epicJson.getBytes());
                    }
                } else {
                    sendNotFound(exchange, "Epic with ID = " + id + " was not found!", 404);
                }
            }
        } else if (exchange.getRequestMethod().equals("POST") && strings.length == 2     //POST-запросы
                && strings[1].equals("epics")) {
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(requestBody);

            if (jsonElement.isJsonObject()) {
                Epic epic = gson.fromJson(requestBody, Epic.class);
                boolean taskManagerNotContains = taskManager.getEpics().stream().noneMatch(epic1 -> epic1.getId() == epic.getId());
                if (!taskManager.findIntersection(epic)) {
                    if (taskManagerNotContains || epic.getId() == null) {
                        taskManager.addEpic(epic);
                        sendText(exchange, "Epic has been added!", 201);
                    } else {
                        taskManager.updateEpic(epic.getId(), epic);
                        sendText(exchange, "Epic has been updated!", 201);
                    }
                } else {
                    sendHasInteractions(exchange, "Interaction was found! Epic wasn't added!", 406);
                }
            }
        } else if (exchange.getRequestMethod().equals("DELETE") && strings.length == 3 && strings[1].equals("epics")) {
            int id;

            try {
                id = Integer.parseInt(strings[2]);
            } catch (NumberFormatException e) {
                sendNotFound(exchange, "Incorrect epic ID!", 404);
                return;
            }

            Epic epic = taskManager.getEpic(id);

            if (epic != null) {
                taskManager.removeEpic(epic.getId());
                sendText(exchange, "Epic with ID = " + id + " was removed!", 200);
            } else {
                sendText(exchange, "Epic with ID = " + id + " wasn't found!", 404);
            }
        }
    }
}
