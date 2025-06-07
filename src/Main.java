import manager.TaskManager;
import task.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Epic epic1 = new Epic("Уборка", null, new ArrayList<>());
        Subtask subtask11 = new Subtask("Пропылесосить", null, Status.NEW, epic1);
        Subtask subtask12 = new Subtask("Помыть посуду", null, Status.DONE, epic1);
        Subtask subtask13 = new Subtask("Прибрать вещи", null, Status.IN_PROGRESS, epic1);
        Epic epic2 = new Epic("Починить машину", null, new ArrayList<>());
        Subtask subtask21 = new Subtask("Почистить карбюратор", null, Status.NEW, epic2);
        Subtask subtask22 = new Subtask("Починить дворники", null, Status.DONE, epic2);
        Subtask subtask23 = new Subtask("Устранить помехи в магнитоле", null, Status.IN_PROGRESS, epic2);
        Task task1 = new Task("Сходить к врачу", null, Status.NEW);
        Task task2 = new Task("Разобрать песню на гитаре", "Kansas - Dust in the wind", Status.IN_PROGRESS);
        Task task3 = new Task("Сходить в тренажерный зал", "День ног", Status.DONE);

        System.out.println("Добавляем 3 задачи:");
        TaskManager.addTask(task1);
        TaskManager.addTask(task2);
        TaskManager.addTask(task3);
        System.out.println(TaskManager.getTasks());

        System.out.println();
        System.out.println("Удаляем задачу:");
        TaskManager.removeTask(task1.getId());
        System.out.println(TaskManager.getTasks());

        System.out.println();
        System.out.println("Обновляем задачу:");
        TaskManager.updateTask(task2.getId(), new Task("Разобрать песню на гитаре", "RHCP - Snow", Status.NEW));
        System.out.println(TaskManager.getTasks());

        System.out.println();
        TaskManager.addEpic(epic1);
        TaskManager.addEpic(epic2);
        System.out.println("Добавили 2 эпика:");
        System.out.println(TaskManager.getEpics());

        TaskManager.addSubtask(subtask11);
        TaskManager.addSubtask(subtask12);
        TaskManager.addSubtask(subtask13);
        TaskManager.addSubtask(subtask21);
        TaskManager.addSubtask(subtask22);
        TaskManager.addSubtask(subtask23);

        System.out.println();
        System.out.println("Добавили им сабтаски:");
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println("Сабтаски: " + TaskManager.getSubtasks());

        TaskManager.removeEpic(epic2.getId());
        System.out.println();
        System.out.println("Удалили эпик");
        System.out.println("Эпики: " + TaskManager.getEpics());
        System.out.println("Сабтаски: " + TaskManager.getSubtasks());

        TaskManager.removeSubtask(subtask11.getId());

        System.out.println();
        System.out.println("Удалили сабтаск из 1 эпика:");
        System.out.println(epic1);
        System.out.println("Сабтаски: " + TaskManager.getSubtasks());

        TaskManager.updateSubtask(subtask13.getId(), new Subtask("Прибрать вещи", null, Status.DONE, epic1));
        System.out.println();
        System.out.println("Изменили статус сабтаска: ");
        System.out.println(epic1);
        System.out.println("Сабтаски: " + TaskManager.getSubtasks());

        TaskManager.clearSubtasks();
        System.out.println();
        System.out.println("Удалили все сабтаски:");
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println("Сабтаски: " + TaskManager.getSubtasks());
    }
}
