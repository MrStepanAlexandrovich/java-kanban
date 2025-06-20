package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    ArrayList<Task> getHistory();
    //Tasks
    HashMap<Integer, Task> getTasks();
    void clearTasks();
    void addTask(Task task);
    void removeTask(int hash);
    Task getTask(int hash);
    void updateTask(int oldTaskHash, Task task);

    //Epics
    HashMap<Integer, Epic> getEpics();
    void clearEpics();
    void addEpic(Epic epic);
    void removeEpic(int hash);
    Epic getEpic(int hash);
    void updateEpic(int oldEpicHash, Epic epic);
    ArrayList<Subtask> getSubtasksOfEpic(Epic epic);

    //Subtasks
    HashMap<Integer, Subtask> getSubtasks();
    void addSubtask(Subtask subtask);
    void removeSubtask(int hash);
    Subtask getSubtask(int hash);
    void updateSubtask(int oldSubtaskHash, Subtask subtask);
    void clearSubtasks();
}
