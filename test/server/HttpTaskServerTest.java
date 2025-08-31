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

        assertEquals(200, response.statusCode());

        List<Task> tasks = taskManager.getTasks();

        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }
}
