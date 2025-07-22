package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path path;
    public FileBackedTaskManager(String filename) {
        super();
        this.path = Paths.get(filename);
    }

    public static void loadFromFile(File file) {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            Stream<String> stream = bufferedReader.lines();
            stream.forEach();
        } catch (IOException e) {

        }
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    private void save() {
        String header = String.format("%-10s %-15s %-20s %-20s %-15s %-7s",
                "id", "type", "name", "status", "description", "epic");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.path)) {
            bufferedWriter.write(header + "\n");
            for (Task task : super.getTasks()) {
                bufferedWriter.write(String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n", task.getId(),
                        "task", task.getName(), task.getStatus(), null, null));
            }

            for (Epic task : super.getEpics()) {
                bufferedWriter.write(String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n", task.getId(),
                        "epic", task.getName(), task.getStatus(), null, null));
            }

            for (Subtask task : super.getSubtasks()) {
                bufferedWriter.write(String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n", task.getId(),
                        "subtask", task.getName(), task.getStatus(), null, null));
            }
        } catch (IOException e) {

        }
    }
}
