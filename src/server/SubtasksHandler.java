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

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] strings = path.split("/");

        if (exchange.getRequestMethod().equals("GET")) {
            if (strings[1].equals("subtasks") && strings.length == 2) {
                List<Subtask> subtasks = taskManager.getSubtasks();
                String subtasksJson = gson.toJson(subtasks);
                sendCorrectRequest(exchange, subtasksJson);

            } else if (strings[1].equals("subtasks") && strings.length == 3) {
                int id;

                try {
                    id = Integer.parseInt(strings[2]);
                } catch (NumberFormatException e) {
                    sendNotFound(exchange,"Incorrect subtask ID!");
                    return;
                }

                Subtask subtask = taskManager.getSubtask(id);
                if (subtask != null) {
                    String subtaskJson = gson.toJson(subtask);

                    sendCorrectRequest(exchange, subtaskJson);
                } else {
                    sendNotFound(exchange, "Subtask with ID = " + id + " was not found!");
                }
            }
        } else if (exchange.getRequestMethod().equals("POST") && strings.length == 2     //POST-запросы
                && strings[1].equals("subtasks")) {
            InputStream inputStream = exchange.getRequestBody();
            String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(requestBody);

            if (jsonElement.isJsonObject()) {
                Subtask subtask = gson.fromJson(requestBody, Subtask.class);
                boolean taskManagerNotContains = taskManager.getSubtasks()
                        .stream()
                        .noneMatch(subtask1 -> subtask1.getId() == subtask.getId());
                if (!taskManager.findIntersection(subtask)) {
                    if (taskManagerNotContains || subtask.getId() == null) {
                        Epic epic = taskManager.getEpic(subtask.getEpicId());
                        if (epic != null) {
                            taskManager.addSubtask(subtask, taskManager.getEpic(subtask.getEpicId()));
                            sendSuccessfullyCreated(exchange, "Subtask has been added!");
                        } else {
                            sendNotFound(exchange, "Epic with ID = " + subtask.getEpicId() +
                                    " wasn't found! Subtask cannot be created without epic!");
                        }

                    } else {
                        taskManager.updateSubtask(subtask.getId(), subtask);
                        sendSuccessfullyCreated(exchange, "Subtask has been updated!");
                    }
                } else {
                    sendHasInteractions(exchange, "Interaction was found! Subtask wasn't added!");
                }
            }
        } else if (exchange.getRequestMethod().equals("DELETE") && strings.length == 3 &&
                strings[1].equals("subtasks")) {
            int id;

            try {
                id = Integer.parseInt(strings[2]);
            } catch (NumberFormatException e) {
                sendNotFound(exchange, "Incorrect task ID!");
                return;
            }

            Subtask subtask = taskManager.getSubtask(id);

            if (subtask != null) {
                taskManager.removeSubtask(subtask.getId());
                sendCorrectRequest(exchange,"Subtask with ID = " + id + " was removed!");
            } else {
                sendNotFound(exchange, "Subtask with ID = " + id + " wasn't found!");
            }
        }
    }
}