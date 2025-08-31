package server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HttpTaskServer {
    private HttpServer httpServer;
    private TaskManager taskManager;
    private final int PORT = 8080;
    private Gson gson;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(new InMemoryTaskManager());

        httpTaskServer.start();
        System.out.println("Сервер запущен");
    }

    public void start() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicsHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(taskManager, gson));

        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public Gson getGson() {
        return gson;
    }

    class TaskListTypeToken extends TypeToken<List<Task>> {

    }

    class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm:ss");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
             if (localDateTime == null) {
                jsonWriter.value("null");
            } else {
                jsonWriter.value(dateTimeFormatter.format(localDateTime));

            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            String date = jsonReader.nextString();
            if (date.equals("null")) {
                return null;
            } else {
                return LocalDateTime.parse(date, dateTimeFormatter);
            }
        }
    }

    class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.value("null");
            } else {
                jsonWriter.value(duration.toMinutes());
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            String durationStr = jsonReader.nextString();
            if (durationStr.equals("null")) {
                return null;
            } else {
                return Duration.ofMinutes(Long.valueOf(durationStr));
            }
        }
    }
}
