import manager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager.addTask(new Task("Помыть машину", "можно в саду", Status.NEW));
        TaskManager.addTask(new Task("Обучаться программированию", null, Status.IN_PROGRESS));
        TaskManager.addTask(new Task("Помыть машину", "можно в саду", Status.NEW));

        Epic epic = new Epic("Съездить в огород", "на машине или на электричке");
        TaskManager.addTask(epic);
        TaskManager.addSubtaskInEpic(epic, new Subtask("Посадить морковку", null, Status.DONE));
        TaskManager.addSubtaskInEpic(epic, new Subtask("Посадить огурцы", null, Status.DONE));

        epic = new Epic("Помыть велик", null);
        TaskManager.addTask(epic);
        TaskManager.addSubtaskInEpic(epic, new Subtask("Найти бензин", null, Status.DONE));
        for (Task task : TaskManager.getTasks().values()) {
            if (task instanceof Epic) {
                System.out.println(task);
                for (Subtask subtask : ((Epic) task).getSubtasks()) {
                    System.out.println("         " + subtask);
                }
            } else {
                System.out.println(task.toString());
            }
        }
    }
}
