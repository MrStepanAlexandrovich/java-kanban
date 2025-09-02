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

public class SubtasksEndpointsTest {
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
    public void updateSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        Epic epic = new Epic("epic", "desc");
        int epicId = taskManager.addEpic(epic);
        int subtaskId = taskManager.addSubtask(new Subtask("subtask", "desc", Status.IN_PROGRESS,
                        LocalDateTime.of(2000, 11, 21, 20, 59), Duration.ofMinutes(29)),
                epic);

        Subtask newSubtask = new Subtask("NEW_SUBTASK", "desc", Status.NEW,
                LocalDateTime.of(2020, 11, 21, 17, 50), Duration.ofMinutes(20));
        newSubtask.setId(subtaskId);

        String newSubtaskJson = gson.toJson(newSubtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(newSubtaskJson))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasks = taskManager.getSubtasks();

        assertEquals(1, subtasks.size());
        assertEquals(taskManager.getSubtask(subtaskId), newSubtask);
    }

    @Test
    public void getSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        Subtask subtask = new Subtask("subtask", "description", Status.NEW,
                LocalDateTime.of(2020, 11, 21, 20, 59), Duration.ofMinutes(70));
        int subtaskId = taskManager.addSubtask(subtask, epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = (String) response.body();

        Subtask subtaskFromJson = gson.fromJson(body, Subtask.class);

        assertEquals(subtask, subtaskFromJson);
    }

    @Test
    public void getSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "desc", Status.NEW,
                LocalDateTime.of(2000, 1, 2, 2, 46), Duration.ofMinutes(20));
        Subtask subtask2 = new Subtask("subtask2", "desc", Status.NEW,
                LocalDateTime.of(2001, 1, 2, 2, 46), Duration.ofMinutes(20));
        Subtask subtask3 = new Subtask("subtask3", "desc", Status.NEW,
                LocalDateTime.of(2002, 1, 2, 2, 46), Duration.ofMinutes(20));

        taskManager.addSubtask(subtask1, epic);
        taskManager.addSubtask(subtask2, epic);
        taskManager.addSubtask(subtask3, epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = (String) response.body();

        List<Subtask> subtasks = gson.fromJson(body, new HttpTaskServer.SubtaskListTypeToken());
        assertEquals(taskManager.getSubtasks(), subtasks);
    }

    @Test
    public void subtaskShouldNotBeFound404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void intersectionShouldBeFoundWhenAddingSubtask406() throws IOException, InterruptedException {
        Task task = new Task("task", "descr", Status.NEW,
                LocalDateTime.of(2020, 11, 1, 10, 20), Duration.ofMinutes(21));
        taskManager.addTask(task);
        Epic epic = new Epic("epic", "d");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("f", "s", Status.NEW,
                LocalDateTime.of(2020, 11, 1, 10, 25), Duration.ofMinutes(20));

        subtask.setEpicId(epicId);

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }
}
