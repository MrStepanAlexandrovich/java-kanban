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
    private static final String HEADER = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "id", "type", "name", "status", "description", "epic");

    static String task = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "91231241", "task", "Пройти_обучение", "DONE", "description", "null");

    static String epic = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "12310398", "epic", "Уборка", "NEW", "description", "null");

    static String subtask = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
            "12310398", "subtask", "Подмести", "NEW", "веником", "-1539297904");

    @BeforeAll
    public static void beforeAll() {
        try {
            file = Files.createTempFile("test", ".txt").toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            bufferedWriter.write(HEADER);
            bufferedWriter.write(task);
            bufferedWriter.write(epic);
            //bufferedWriter.write(subtask);

        } catch (IOException e) {
            e.printStackTrace();
        }

        fbtm = Managers.loadFromFile(file);
    }

    @Test
    public void managerShouldAddTasksSubtasksAndEpicsFromFile() {
        // assertEquals(1, fbtm.getSubtasks().size());
        assertEquals(1, fbtm.getEpics().size());
        assertEquals(1, fbtm.getTasks().size());
        Epic epic = fbtm.getEpics().get(0);
        System.out.println(fbtm.getEpics().get(0).getId());
    }

    @Test
    public void managerShouldLoadFromEmptyFile() {

    }
}
