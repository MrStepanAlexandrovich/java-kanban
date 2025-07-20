import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import task.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task(String.valueOf(1),
                String.valueOf(2), Status.NEW);
        Task task2 = new Task(String.valueOf(2),
                String.valueOf(3), Status.NEW);
        Task task3 = new Task(String.valueOf(3),
                String.valueOf(4), Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());

        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
