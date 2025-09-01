package server;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
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

public class HttpTaskServerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = server.getGson();

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
    public void addSubtaskAndEpicTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/epics");

        Epic epic = new Epic("epic", "desc");
        String epicJson = gson.toJson(epic);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse response = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epics = taskManager.getEpics();

        assertEquals(1, epics.size());
        assertEquals(epic.getName(), epics.get(0).getName());

        Subtask subtask = new Subtask("subtask", "desc", Status.IN_PROGRESS,
                LocalDateTime.of(2020, 11, 21, 20, 59), Duration.ofMinutes(20));

        subtask.setEpicId(epics.get(0).getId());
        String subtaskJson = gson.toJson(subtask);

        URI url2 = URI.create("http://localhost:8080/subtasks");

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasks = taskManager.getSubtasks();

        assertEquals(1, subtasks.size());
        assertEquals(subtask.getName(), subtasks.get(0).getName());
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
    public void deleteEpic() throws IOException, InterruptedException {
        int epicId = taskManager.addEpic(new Epic("epic", "desc"));

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/epics/" + epicId);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();

        HttpResponse response = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertNull(taskManager.getTask(epicId));
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc");
        int epicId = taskManager.addEpic(epic);
        int subtaskId = taskManager.addSubtask(new Subtask("subtask", "desc", Status.IN_PROGRESS,
                LocalDateTime.of(2000, 11, 21, 20, 59), Duration.ofMinutes(29)),
                epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/subtasks/" + subtaskId);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();

        HttpResponse response = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertNull(taskManager.getSubtask(subtaskId));
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
}
