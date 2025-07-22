import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {/*
        Task task1 = new Task("task1", "desc1", Status.NEW);
        Task task2 = new Task("task2", "desc2", Status.NEW);
        Epic epic1 = new Epic("epic1", "desc", new ArrayList<>());
        Subtask subtask1 = new Subtask("subtask1", "desc1", Status.NEW, epic1);
        Subtask subtask2 = new Subtask("subtask2", "desc2", Status.IN_PROGRESS, epic1);
        Subtask subtask3 = new Subtask("subtask3", "desc3", Status.DONE, epic1);
        Epic epic2 = new Epic("epic2", "desc2", new ArrayList<>());

        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        taskManager.addEpic(epic2);

        taskManager.getTask(task1.getId());

        System.out.println();
        System.out.println("1");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.getSubtask(subtask3.getId());

        System.out.println();
        System.out.println("2");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.getTask(task2.getId());

        System.out.println();
        System.out.println("3");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.getEpic(epic2.getId());

        System.out.println();
        System.out.println("4");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.getSubtask(subtask2.getId());

        System.out.println();
        System.out.println("5");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.getSubtask(subtask1.getId());

        System.out.println();
        System.out.println("6");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.getEpic(epic1.getId());

        System.out.println();
        System.out.println("7");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.removeSubtask(subtask1.getId());

        System.out.println();
        System.out.println("После удаления сабтаска:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }


        System.out.println();
        System.out.println("После добавления всех задач:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.removeEpic(epic1.getId());

        System.out.println();
        System.out.println("После удаления эпика:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        taskManager.removeTask(task1.getId());

        System.out.println();
        System.out.println("После удаления задачи:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());*/

        FileBackedTaskManager fbtm = new FileBackedTaskManager("backup.txt");
        fbtm.addTask(new Task("asdfasd", "asdfasfd", Status.DONE));
        fbtm.addEpic(new Epic("epic", "desc", new ArrayList<>()));
    }
}
