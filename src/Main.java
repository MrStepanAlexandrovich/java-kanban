import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        int idTask1 = taskManager.addTask(new Task("Zadanie", "descrip", Status.NEW));
        int idEpic1 = taskManager.addEpic(new Epic("Epichnoe zadanie", "desc", new ArrayList<>()));
        int idEpic2 = taskManager.addEpic(new Epic("Epichnoe zad2", "desc", new ArrayList<>()));
        int idEpic = taskManager.addEpic(new Epic("Epichnoe zada1", "desc", new ArrayList<>()));
        int idSubtask = taskManager.addSubtask(new Subtask("nameSUb", "desc", Status.NEW, taskManager.getEpic(idEpic)));

        taskManager.getEpic(idEpic1);
        taskManager.getEpic(idEpic2);
        taskManager.getTask(idTask1);

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.getSubtask(idSubtask);
        System.out.println();

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
