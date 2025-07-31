package manager;

import task.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

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
            throw new ManagerSaveException("Ошибка при сохранении в файл!");
        }
    }

    private static String taskToString(Task task) {
        String string;
        Integer epicId;
        String type;

        switch (task.getType()) {
            case TASK -> {
                epicId = null;
                type = "task";
            }
            case EPIC -> {
                epicId = null;
                type = "epic";
            }
            case SUBTASK -> {
                epicId = ((Subtask) task).getEpic().getId();
                type = "subtask";
            }
            default -> {
                epicId = null;
                type = null;
            }
        }

        string = String.format("%-10s %-15s %-20s %-20s %-15s %-7s\n", task.getId(),
                type, task.getName(), task.getStatus(), task.getDescription(), epicId);
        return string;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();//Читаем шапку таблицы
            List<String> strings = new ArrayList<>();

            while (bufferedReader.ready()) {
                strings.add(bufferedReader.readLine());
            }

            for (String string : strings) {
                if (!string.isBlank()) {
                    Task task = stringToTask(string, fileBackedTaskManager);
                    int maxCounter = Math.max(fileBackedTaskManager.getCounter(), task.getId());
                    fileBackedTaskManager.setCounter(task.getId() - 1); //В методах add counter увеличивается на 1

                    switch (task.getType()) {
                        case Type.TASK -> fileBackedTaskManager.addTask(task);
                        case Type.EPIC -> fileBackedTaskManager.addEpic((Epic) task);
                        case Type.SUBTASK -> fileBackedTaskManager.addSubtask((Subtask) task);
                    }

                    fileBackedTaskManager.setCounter(maxCounter);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBackedTaskManager;
    }

    private static Task stringToTask(String string, FileBackedTaskManager fileBackedTaskManager) {
        Task task = null;
        if (!string.isBlank()) {
            String[] strings = string.split("\s+");
            task = switch (strings[1].toLowerCase()) {
                case "task" -> new Task(strings[2], strings[4], Status.valueOf(strings[3]));
                case "subtask" -> new Subtask(strings[2], strings[4], Status.valueOf(strings[3]),
                        fileBackedTaskManager.getEpic(Integer.parseInt(strings[5])));
                case "epic" -> new Epic(strings[2], strings[4], new ArrayList<>());
                default -> null;
            };
            try {
                task.setId(Integer.parseInt(strings[0]));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return task;
    }

    //Пользовательский сценарий
    public static void main(String[] args) {
        /*TaskManager taskManager = Managers.getDefault();
        int epicId = taskManager.addEpic(new Epic("epic", "description", new ArrayList<>()));
        taskManager.addTask(new Task("Zadacha", "Opisanie", Status.DONE));
        taskManager.addSubtask(new Subtask("subtask", "desc", Status.NEW,
                taskManager.getEpic(epicId)));    */            //Сохраняем в файл

        TaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(Paths.get("savedTasks.txt").toFile());
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

