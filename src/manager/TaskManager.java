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

    public static void updateTask(int OldTaskHash, Task task) {
        tasks.put(task.getId(), task);
        tasks.remove(OldTaskHash);
    }

    public static void clear() {
        tasks.clear();

        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
        epics.clear();

        subtasks.clear();
    }

    //Epics
    public static HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public static void addEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.hashCode(), epic);
        }

        ArrayList<Subtask> subtasks = epic.getSubtasks(); //?????????????????????7
    }

    public static void removeEpic(int hash) {
        Epic epic = epics.get(hash);

        ArrayList<Subtask> subtasks = epic.getSubtasks();
        for (Subtask subtask : subtasks) {
            TaskManager.subtasks.remove(subtask.getId()); //Удаляем из списка в TaskManager
        }

        epic.getSubtasks().clear(); //Удаляем сабтаски из объекта
        epics.remove(hash);
    }

    public static Epic getEpic(int hash) {
        return epics.get(hash);
    }

    public static void updateEpic(int OldEpicHash, Epic epic) {


        epics.put(epic.getId(), epic);
        epics.remove(OldEpicHash);
    }

    public static ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        return epic.getSubtasks();
    }

    //Subtasks
    public static HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public static void addSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.hashCode(), subtask);
        }
    }

    public static void removeSubtask(int hash) {
        subtasks.remove(hash);
    }

    public static Subtask getSubtask(int hash) {
        return subtasks.get(hash);
    }

    public static void updateSubtask(int OldSubtaskHash, Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        subtasks.remove(OldSubtaskHash);
    }


    /*public static void addSubtaskInEpic(Epic epic, Subtask subtask) {
        if (epic != null && subtask != null) {
            ArrayList<Subtask> subtasks = epic.getSubtasks();
            subtasks.add(subtask);
            subtask.setEpic(epic);
            epic.refreshStatus();
        }
    }*/





}
