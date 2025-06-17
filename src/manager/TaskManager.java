package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    //Tasks
    public HashMap<Integer, Task> getTasks();
    public void clearTasks();
    public void addTask(Task task);
    public void removeTask(int hash);
    public Task getTask(int hash);
    public void updateTask(int oldTaskHash, Task task);

    //Epics
    public HashMap<Integer, Epic> getEpics();
    public void clearEpics();
    public void addEpic(Epic epic);
    public void removeEpic(int hash);
    public Epic getEpic(int hash);
    public void updateEpic(int oldEpicHash, Epic epic);
    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic);

    //Subtasks
    public HashMap<Integer, Subtask> getSubtasks();
    public void addSubtask(Subtask subtask);
    public void removeSubtask(int hash);
    public Subtask getSubtask(int hash);
    public void updateSubtask(int oldSubtaskHash, Subtask subtask);
    public void clearSubtasks();
}
