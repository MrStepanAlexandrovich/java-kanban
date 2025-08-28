package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;
import java.io.OutputStream;
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
                    sendNotFound(exchange, "Incorrect epic ID!");
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
                    sendNotFound(exchange, "Epic with ID = " + id + " was not found!");
                }
            }
        }
    }
}