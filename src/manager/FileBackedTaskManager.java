package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path path;
    public FileBackedTaskManager(String filename) {
        super();
        this.path = Paths.get(filename);
    }

    public static void loadFromFile(File file) {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine(); //Читаем шапку таблицы
            while (bufferedReader.ready()) {
                Task task = stringToTask(bufferedReader.readLine());
                if (task instanceof Epic) {
                    this.addEpic((Epic) task);
                }
            }
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
        String header = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
                "id", "type", "name", "status", "description", "epic");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.path)) {
            bufferedWriter.write(header);
            for (Task task : super.getTasks()) {
                bufferedWriter.write(taskToString(task));
            }

            for (Epic epic : super.getEpics()) {
                bufferedWriter.write(taskToString(epic));
            }

            for (Subtask subtask : super.getSubtasks()) {
                bufferedWriter.write(taskToString(subtask));
            }
        } catch (IOException e) {

        }
    }

    private static String taskToString(Task task) {
        return String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n", task.getId(),
                "task", task.getName(), task.getStatus(), null, null);
    }

    private static Task stringToTask(String string) {
        String[] strings = string.split(" ");
        switch (strings[1]) {
            case "task" -> {
                return new Task(strings[0], strings[4], null);
            }
            case "subtask" -> {
                new Subtask(strings[0], strings[4], null, null);
            }
            case "Epic" -> {
                return new Epic(strings[0], strings[4], new ArrayList<>());
            }
        }
        return null;
    }
}
