package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path path;
    public FileBackedTaskManager(Path path) {
        super();
        this.path = path;
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
        String header = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
                "id", "type", "name", "status", "description", "epic");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.path)) {
            bufferedWriter.write(header);
            for (Epic epic : super.getEpics()) {
                bufferedWriter.write(taskToString(epic));
            }

            for (Task task : super.getTasks()) {
                bufferedWriter.write(taskToString(task));
            }

            for (Subtask subtask : super.getSubtasks()) {
                bufferedWriter.write(taskToString(subtask));
            }
        } catch (IOException e) {

        }
    }

    private static String taskToString(Task task) {
        String string = null;
        String type;
        Integer epic;
        if (task instanceof Epic) {
            type = "epic";
            epic = null;
        } else if (task instanceof Subtask) {
            type = "subtask";
            epic = ((Subtask) task).getEpic().getId();
        } else {
            type = "task";
            epic = null;
        }
        string = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n", task.getId(),
                type, task.getName(), task.getStatus(), task.getDescription(), epic);
        return string;
    }
}
