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

public class EpicsEndpointsTest {
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
    public void updateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc");

        HttpClient client = HttpClient.newHttpClient();

        int epicId = taskManager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/epics");

        Epic newEpic = new Epic("NEW_EPIC", "new_desc");
        newEpic.setId(epic.getId());

        String newEpicJson = gson.toJson(newEpic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(newEpicJson))
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epics = taskManager.getEpics();

        assertEquals(1, epics.size());
        assertEquals(taskManager.getEpic(epicId), newEpic);
    }

    @Test
    public void getEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "description");
        Subtask subtask = new Subtask("subtask", "description", Status.NEW,
                LocalDateTime.of(2020, 11, 21, 20, 59), Duration.ofMinutes(70));
        int epicId = taskManager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = (String) response.body();

        Epic epicFromJson = gson.fromJson(body, Epic.class);

        assertEquals(epic, epicFromJson);
    }

    @Test
    public void getEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "desc");
        Epic epic2 = new Epic("epic2", "desc");
        Epic epic3 = new Epic("epic3", "desc");

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);

        Subtask subtask1 = new Subtask("subtask1", "desc", Status.NEW,
                LocalDateTime.of(2000, 1, 2, 2, 46), Duration.ofMinutes(20));
        Subtask subtask2 = new Subtask("subtask2", "desc", Status.NEW,
                LocalDateTime.of(2001, 1, 2, 2, 46), Duration.ofMinutes(20));
        Subtask subtask3 = new Subtask("subtask3", "desc", Status.NEW,
                LocalDateTime.of(2002, 1, 2, 2, 46), Duration.ofMinutes(20));

        taskManager.addSubtask(subtask1, epic1);
        taskManager.addSubtask(subtask2, epic2);
        taskManager.addSubtask(subtask3, epic3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = (String) response.body();

        List<Epic> epics = gson.fromJson(body, new HttpTaskServer.EpicsListTypeToken());
        assertEquals(taskManager.getEpics(), epics);
    }

    @Test
    public void getSubtasksOfEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc");
        int epicId = taskManager.addEpic(epic);

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
        URI url = URI.create("http://localhost:8080/epics/" + epicId + "/subtasks");
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
}
