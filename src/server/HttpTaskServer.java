package server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
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
    private static TaskManager taskManager;
    private static final int PORT = 8080;
    private static Gson gson;

    static {
        taskManager = Managers.getDefault();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .serializeNulls()
                .setPrettyPrinting()
                .create();
       taskManager.addTask(new Task("asdfasdf", "description", Status.IN_PROGRESS));
       int epicId = taskManager.addEpic(new Epic("epic", "description"));
       taskManager.addSubtask(new Subtask("subtask", "desc", Status.NEW, null),
               taskManager.getEpic(epicId));
    }

    public static void main(String[] args) {
        HttpServer httpServer;
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/subtasks", new SubtasksHandler());
        httpServer.createContext("/epics", new EpicsHandler());

        httpServer.start();
        System.out.println("Сервер запущен");
    }





    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static Gson getGson() {
        return gson;
    }

    static class TaskListTypeToken extends TypeToken<List<Task>> {

    }

    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:MM:ss");

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
            if (jsonReader.nextString().equals("null")) {
                return null;
            } else {
                return LocalDateTime.parse(jsonReader.nextString(), dateTimeFormatter);
            }
        }
    }

    static class DurationTypeAdapter extends TypeAdapter<Duration> {
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
            if (jsonReader.nextString().equals("null")) {
                return null;
            } else {
                return Duration.ofMinutes(Long.valueOf(jsonReader.nextString()));
            }
        }
    }
}
