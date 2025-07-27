package manager;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    static FileBackedTaskManager fbtm = null;
    static File file = null;
    private String HEADER = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "id", "type", "name", "status", "description", "epic");

    static String task = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "1", "task", "Пройти_обучение", "DONE", "description", "null");

    static String epic = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "2", "epic", "Уборка", "NEW", "description", "null");

    static String subtask = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "3", "subtask", "Подмести", "NEW", "веником", "2");

    @BeforeEach
    public void beforeEach() {
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

        fbtm = Managers.loadFromFile(file);
    }

    @Test
    public void managerShouldAddTasksSubtasksAndEpicsFromFile() {
        assertEquals(1, fbtm.getSubtasks().size());
        assertEquals(1, fbtm.getEpics().size());
        assertEquals(1, fbtm.getTasks().size());
    }

    @Test
    public void managerShouldLoadFromEmptyFile() {
        try {
            fbtm = Managers.loadFromFile(File.createTempFile("empty", ".txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(0, fbtm.getTasks().size());
    }
}
