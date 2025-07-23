package manager;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Subtask;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager fbtm = null;
    File file = null;
    private static final String HEADER = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "id", "type", "name", "status", "description", "epic");

    String task = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "id", "task", "Пройти_обучение", "DONE", "description", "null");

    String epic = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "12310398", "epic", "Уборка", "NEW", "description", "null");

    String subtask = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "12310398", "subtask", "Подмести", "NEW", "веником", "-1994460504");

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
}
