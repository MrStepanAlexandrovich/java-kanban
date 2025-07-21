package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    void clearHistory();

    //Tasks
    List<Task> getTasks();

    void clearTasks();

    int addTask(Task task);

    void removeTask(int hash);

    Task getTask(int hash);

    int updateTask(int oldTaskHash, Task task);

    //Epics
    List<Epic> getEpics();

    void clearEpics();

    int addEpic(Epic epic);

    void removeEpic(int hash);

    Epic getEpic(int hash);

    int updateEpic(int oldEpicHash, Epic epic);

    ArrayList<Subtask> getSubtasksOfEpic(Epic epic);

    //Subtasks
    List<Subtask> getSubtasks();

    int addSubtask(Subtask subtask);

    void removeSubtask(int hash);

    Subtask getSubtask(int hash);

    int updateSubtask(int oldSubtaskHash, Subtask subtask);

    void clearSubtasks();
}
