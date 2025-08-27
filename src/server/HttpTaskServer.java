package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import task.Status;
import task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Handler;

public class HttpTaskServer {
    private static TaskManager taskManager;
    private static final int PORT = 8080;
    private static Gson gson;

    static {
        taskManager = Managers.getDefault();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        taskManager.addTask(new Task("asdfasdf", "description", Status.IN_PROGRESS));
    }

    public static void main(String[] args) {
        HttpServer httpServer;
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            System.out.println("Сервер запущен");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpServer.createContext("/tasks", new GetTasksHandler());
        httpServer.start();
    }

     static class GetTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String[] strings = path.split("/");

            /*if (exchange.getRequestMethod().equals("GET") && )*/
            List<Task> tasks = taskManager.getTasks();
            String tasksJson = gson.toJson(tasks, new TaskListTypeToken().getClass());
            exchange.sendResponseHeaders(200, 0);
            try {
                OutputStream os = exchange.getResponseBody();
                os.write(tasksJson.getBytes(StandardCharsets.UTF_8));
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    static class TaskListTypeToken extends TypeToken<List<Task>> {

    }
}
