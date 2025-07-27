package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path path;

    public FileBackedTaskManager(File file) {
        super();
        this.path = file.toPath();
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);

        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }

        return id;
    }

    private void save() throws ManagerSaveException {
        String header = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n",
                "id", "type", "name", "status", "description", "epic");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.path)) {
            bufferedWriter.write(header);
            for (Epic epic : super.getEpics()) {
                bufferedWriter.write(taskToString(epic));
            }

            for (Subtask subtask : super.getSubtasks()) {
                bufferedWriter.write(taskToString(subtask));
            }

            for (Task task : super.getTasks()) {
                bufferedWriter.write(taskToString(task));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл!");
        }
    }

    private static String taskToString(Task task) {
        String string;
        Integer epicId;
        String type;
        if (task instanceof Epic) {
            epicId = null;
            type = "epic";
        } else if (task instanceof Subtask) {
            epicId = ((Subtask) task).getEpic().getId();
            type = "subtask";
        } else {
            epicId = null;
            type = "task";
        }
        string = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n", task.getId(),
                type, task.getName(), task.getStatus(), task.getDescription(), epicId);
        return string;
    }


    //Пользовательский сценарий
    public static void main(String[] args) {
        TaskManager taskManager = new FileBackedTaskManager(Paths.get("1.txt").toFile());
        int epicId = taskManager.addEpic(new Epic("epic", "description", new ArrayList<>()));
        taskManager.addTask(new Task("Zadacha", "Opisanie", Status.DONE));
        taskManager.addSubtask(new Subtask("subtask", "desc", Status.DONE,
                taskManager.getEpic(epicId)));                   //Сохраняем в файл

        TaskManager loadedTaskManager = Managers.loadFromFile(Paths.get("1.txt").toFile());
        for (Task task : loadedTaskManager.getTasks()) {  //Загружаем из файла
            System.out.println(task);
        }

        for (Epic epic : loadedTaskManager.getEpics()) {
            System.out.println(epic);
        }

        for (Subtask subtask : loadedTaskManager.getSubtasks()) {
            System.out.println(subtask);
        }
    }
}

