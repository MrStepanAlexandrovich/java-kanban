package manager;

import task.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;
    public static final String FORMATTER = "%-10s %-15s %-20s %-20s %-15s %-7s %-20s %-10s\n";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm|dd.MM.yyyy");

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
    public int addSubtask(Subtask subtask, Epic epic) {
        int id = super.addSubtask(subtask, epic);

        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }

        return id;
    }

    private void save() throws ManagerSaveException {
        String header = String.format(FORMATTER, "id", "type", "name", "status", "description", "epic", "start_time",
                "duration");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(this.path)) {
            bufferedWriter.write(header);
            for (Task task : getTasks()) {
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
        String start = (task.getStartTime() == null) ? null : task.getStartTime().format(TIME_FORMATTER);
        String duration = (task.getDuration() != null) ? String.valueOf(task.getDuration().toMinutes())
                : "null";

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
                epicId = ((Subtask) task).getEpicId();
                type = "subtask";
            }
            default -> {
                epicId = null;
                type = null;
            }
        }

        string = String.format(FORMATTER, task.getId(), type, task.getName(), task.getStatus(), task.getDescription(),
                epicId, start, duration);
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
                        case Type.SUBTASK -> fileBackedTaskManager.addSubtask((Subtask) task,
                                fileBackedTaskManager.getEpic(((Subtask) task).getEpicId()));
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
            String type = strings[1];
            String name = strings[2];
            String status = strings[3];
            String description = (strings[4].equals("null")) ? null : strings[4];
            String epic = strings[5];
            LocalDateTime startTime = (!strings[6].equals("null")) ? LocalDateTime.parse(strings[6], TIME_FORMATTER)
                    : null;
            Duration duration = (!strings[7].equals("null")) ? Duration.ofMinutes(Long.valueOf(strings[7])) : null;

            task = switch (type.toLowerCase()) {
                case "task" -> new Task(name, description, Status.valueOf(status), startTime, duration);
                case "subtask" -> { Subtask subtask = new Subtask(name, description, Status.valueOf(status),
                        startTime, duration);
                    subtask.setEpicId(Integer.parseInt(epic));
                    yield subtask;
                }
                case "epic" -> new Epic(name, description);
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
        int epicId1 = taskManager.addEpic(new Epic("epic", "description"));
        int epicId2 = taskManager.addEpic(new Epic("epic2", "description"));

        taskManager.addTask(new Task("Zadacha", "Opisanie", Status.DONE, LocalDateTime.now(),
                Duration.ofMinutes(60)));
        taskManager.addSubtask(new Subtask("subtask", "desc", Status.NEW,
                taskManager.getEpic(epicId1), LocalDateTime.of(2020, 11, 21, 23, 0),
                Duration.ofMinutes(50)));*/               //Сохраняем в файл

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

        System.out.println();
        loadedTaskManager.getPrioritizedTasks()
                .stream()
                .forEach(System.out::println);
    }
}

