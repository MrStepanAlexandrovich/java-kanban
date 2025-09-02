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

public class PrioritizedTasksTest {
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
    public void getPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "desc1", Status.NEW,
                LocalDateTime.of(2000, 11, 20, 20, 0), Duration.ofMinutes(20));
        Task task2 = new Task("task2", "desc2", Status.IN_PROGRESS,
                LocalDateTime.of(1999, 11, 20, 10, 0), Duration.ofMinutes(20));
        Task task3 = new Task("task3", "desc3", Status.DONE,
                LocalDateTime.of(2001, 11, 20, 2, 0), Duration.ofMinutes(20));

        int id1 = taskManager.addTask(task1);
        int id2 = taskManager.addTask(task2);
        int id3 = taskManager.addTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = (String) response.body();

        List<Task> prioritized = gson.fromJson(body, new HttpTaskServer.TaskListTypeToken());

        assertEquals(taskManager.getPrioritizedTasks(), prioritized);
    }

    @Test
    public void taskShouldNotBeFound404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
