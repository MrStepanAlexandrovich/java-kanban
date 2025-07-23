package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine(); //Читаем шапку таблицы
            while (bufferedReader.ready()) {
                Task task = stringToTask(bufferedReader.readLine(), fileBackedTaskManager);
                if (task instanceof Epic) {
                    fileBackedTaskManager.addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    fileBackedTaskManager.addSubtask((Subtask) task);
                } else if (task instanceof Task) {
                    fileBackedTaskManager.addTask(task);
                }
            }
        } catch (IOException e) {

        }
        return fileBackedTaskManager;
    }

    private static Task stringToTask(String string, FileBackedTaskManager fileBackedTaskManager) {
        String[] strings = string.split("\s+");
        Task task = switch (strings[1].toLowerCase()) {
            case "task" -> new Task(strings[2], strings[4], Status.valueOf(strings[3]));
            case "subtask" -> new Subtask(strings[2], strings[4], Status.valueOf(strings[3]),
                    fileBackedTaskManager.getEpic(Integer.parseInt(strings[5])));
            case "epic" -> new Epic(strings[2], strings[4], new ArrayList<>());
            default -> null;
        };
        return task;
    }
}
