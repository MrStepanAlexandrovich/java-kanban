package manager;

import java.util.ArrayList;
import java.util.HashMap;
import task.*;

public class TaskManager {
    private static final HashMap<Integer, Task> tasks = new HashMap<>();
    private static final HashMap<Integer, Epic> epics = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    //Tasks
    public static HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public static void clearTasks() {
        tasks.clear();
    }

    public static void addTask(Task task) {
        if (task != null) {
            tasks.put(task.hashCode(), task);
        }
    }

    public static void removeTask(int hash) {
        tasks.remove(hash);
    }

    public static Task getTask(int hash) {
        return tasks.get(hash);
    }

    public static void updateTask(int oldTaskHash, Task task) {
        tasks.put(task.getId(), task);
        tasks.remove(oldTaskHash);
    }


    //Epics
    public static HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public static void clearEpics() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
        subtasks.clear();
        epics.clear();
    }

    public static void addEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.hashCode(), epic);
        }

        ArrayList<Subtask> newSubtasks = epic.getSubtasks();
        for (Subtask subtask : newSubtasks) {
            subtasks.put(subtask.getId(), subtask);    //добавляем сабтаски в список в TaskManager
        }
    }

    public static void removeEpic(int hash) {
        Epic epic = epics.get(hash);

        ArrayList<Subtask> subtasks = epic.getSubtasks();
        for (Subtask subtask : subtasks) {
            TaskManager.subtasks.remove(subtask.getId()); //Удаляем сабтаски эпика из списка в TaskManager
        }

        epic.getSubtasks().clear(); //Удаляем сабтаски из объекта

        epics.remove(hash); //Удаляем эпик
    }

    public static Epic getEpic(int hash) {
        return epics.get(hash);
    }

    public static void updateEpic(int oldEpicHash, Epic epic) {
        removeEpic(oldEpicHash);
        addEpic(epic);
    }

    public static ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        return epic.getSubtasks();
    }

    public static void refreshStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            ArrayList<Status> subtaskStatusList = new ArrayList<>();
            for (Subtask subtask : epic.getSubtasks()) {
                subtaskStatusList.add(subtask.getStatus());
            }

            if (!subtaskStatusList.contains(Status.DONE) && !subtaskStatusList.contains(Status.IN_PROGRESS)) {
                epic.setStatus(Status.NEW);
            } else if (!subtaskStatusList.contains(Status.NEW) && !subtaskStatusList.contains(Status.IN_PROGRESS)) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    //Subtasks
    public static HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public static void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            refreshStatus(epic);
        }
    }

    public static void addSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.hashCode(), subtask);
        }
        Epic epic = subtask.getEpic();
        epic.getSubtasks().add(subtask);
        refreshStatus(epic);
    }

    public static void removeSubtask(int hash) {
        Subtask subtask = subtasks.get(hash);
        Epic epic = subtask.getEpic();

        epic.getSubtasks().remove(subtask); //Удаляем из эпика
        refreshStatus(epic);

        subtasks.remove(hash); //Удаляем из списка
    }

    public static Subtask getSubtask(int hash) {
        return subtasks.get(hash);
    }

    public static void updateSubtask(int oldSubtaskHash, Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        removeSubtask(oldSubtaskHash);
    }
}
