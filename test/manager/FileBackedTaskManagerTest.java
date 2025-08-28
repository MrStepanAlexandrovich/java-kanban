package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static manager.FileBackedTaskManager.FORMATTER;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static FileBackedTaskManager fbtm = null;
    private static File file = null;

    @Override
    public FileBackedTaskManager createTaskManager() {
        try {
            return new FileBackedTaskManager(File.createTempFile("FileBackedTaskManagerTest", ".txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final String HEADER = String.format(FORMATTER,
            "id", "type", "name", "status", "description", "epic", "start_time", "duration");

    static String task = String.format(FORMATTER,
            "1", "task", "Пройти_обучение", "DONE", "description", "null", "null", "null");

    static String epic = String.format(FORMATTER,
            "2", "epic", "Уборка", "NEW", "description", "null", "null", "null");

    static String subtask = String.format(FORMATTER,
            "3", "subtask", "Подмести", "NEW", "веником", "2", "null", "null");

    @Test
    public void managerShouldAddTasksSubtasksAndEpicsFromFile() {
        try {
            file = Files.createTempFile("test", ".txt").toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            bufferedWriter.write(HEADER);
            bufferedWriter.write(task);
            bufferedWriter.write(epic);
            bufferedWriter.write(subtask);

        } catch (IOException e) {
            e.printStackTrace();
        }

        fbtm = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, fbtm.getSubtasks().size());
        assertEquals(1, fbtm.getEpics().size());
        assertEquals(1, fbtm.getTasks().size());
    }

    @Test
    public void managerShouldLoadFromEmptyFile() {
        try {
            file = Files.createTempFile("test", ".txt").toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            bufferedWriter.write(HEADER);
            bufferedWriter.write(task);
            bufferedWriter.write(epic);
            bufferedWriter.write(subtask);

        } catch (IOException e) {
            e.printStackTrace();
        }

        fbtm = FileBackedTaskManager.loadFromFile(file);
        try {
            fbtm = FileBackedTaskManager.loadFromFile(File.createTempFile("empty", ".txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(0, fbtm.getTasks().size());
        assertEquals(0, fbtm.getSubtasks().size());
        assertEquals(0, fbtm.getEpics().size());
    }

    @Test
    public void saveTest() {
        TaskManager fileBackedTaskManager = null;
        File file = null;
        try {
            file = File.createTempFile("save", ".txt");
            fileBackedTaskManager = new FileBackedTaskManager(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileBackedTaskManager.addTask(new Task("task", null, Status.DONE,
                LocalDateTime.of(2000, 11, 21, 12, 0), Duration.ofMinutes(50)));
        Epic epic = new Epic("epic", "desc");
        fileBackedTaskManager.addEpic(epic);
        fileBackedTaskManager.addSubtask(new Subtask("subtask", "description", Status.IN_PROGRESS,
                LocalDateTime.of(2001, 4, 3, 21, 20),
                Duration.ofMinutes(25)), epic);

            TaskManager fileBackedTaskManagerRead = FileBackedTaskManager.loadFromFile(file);
            assertEquals(fileBackedTaskManager.getTasks().get(0), fileBackedTaskManagerRead.getTasks().get(0));
            assertEquals(fileBackedTaskManager.getSubtasks(), fileBackedTaskManagerRead.getSubtasks());
            assertEquals(fileBackedTaskManager.getEpics(), fileBackedTaskManagerRead.getEpics());

    }
}
