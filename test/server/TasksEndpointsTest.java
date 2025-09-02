package server;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TasksEndpointsTest {
    private TaskManager taskManager = new InMemoryTaskManager();
    private HttpTaskServer server = new HttpTaskServer(taskManager);
    private Gson gson = server.getGson();

    @BeforeEach
    public void setUp() {
        server.start();
    }

    @AfterEach
    public void afterEach() {
        server.stop();
    }

    @Test
    public void addTaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        Task task = new Task("task", "desc", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(50));
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasks = taskManager.getTasks();

        assertEquals(1, tasks.size());
        assertEquals(task.getName(), tasks.get(0).getName());
    }

    @Test
    public void deleteTaskTest() throws IOException, InterruptedException {
        int taskId = taskManager.addTask(new Task("task", "desc", Status.IN_PROGRESS));

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks/" + taskId);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();

        HttpResponse response = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertNull(taskManager.getTask(taskId));
    }


    @Test
    public void updateTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        Task task = new Task("task", "desc", Status.NEW,
                LocalDateTime.of(2020, 11, 20, 19, 1), Duration.ofMinutes(50));
        int taskId = taskManager.addTask(task);

        Task newTask = new Task("NEW", "desc", Status.NEW,
                LocalDateTime.of(2019, 11, 20, 19, 1), Duration.ofMinutes(132));
        newTask.setId(taskId);

        String newTaskJson = gson.toJson(newTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(newTaskJson))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasks = taskManager.getTasks();

        assertEquals(1, tasks.size());
        assertEquals(taskManager.getTask(taskId), newTask);
    }

    @Test
    public void getTaskById() throws IOException, InterruptedException {
        Task task = new Task("task", "description", Status.NEW,
                LocalDateTime.of(2020, 11, 21, 20, 59), Duration.ofMinutes(70));
        int id = taskManager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = (String) response.body();

        Task taskFromJson = gson.fromJson(body, Task.class);

        assertEquals(task, taskFromJson);
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "desc1", Status.NEW,
                LocalDateTime.of(2000, 11, 20, 20, 0), Duration.ofMinutes(20));
        Task task2 = new Task("task2", "desc2", Status.IN_PROGRESS,
                LocalDateTime.of(2000, 11, 20, 10, 0), Duration.ofMinutes(20));
        Task task3 = new Task("task3", "desc3", Status.DONE,
                LocalDateTime.of(2000, 11, 20, 2, 0), Duration.ofMinutes(20));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = (String) response.body();

        List<Task> tasks = gson.fromJson(body, new HttpTaskServer.TaskListTypeToken());

        assertEquals(taskManager.getTasks(), tasks);
    }

    @Test
    public void intersectionShouldBeFoundWhenAddingTask406() throws IOException, InterruptedException {
        Task task1 = new Task("task", "descr", Status.NEW,
                LocalDateTime.of(2020, 11, 1, 10, 20), Duration.ofMinutes(21));
        taskManager.addTask(task1);

        Task task2 = new Task("task", "descr", Status.NEW,
                LocalDateTime.of(2020, 11, 1, 10, 30), Duration.ofMinutes(21));

        String task2Json = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }
}
