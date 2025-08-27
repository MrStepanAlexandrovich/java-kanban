package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getHistory();

    void clearHistory();

    //Tasks
    List<Task> getTasks();

    void clearTasks();

    int addTask(Task task);

    void removeTask(int id);

    Task getTask(int id);

    int updateTask(int id, Task task);

    //Epics
    List<Epic> getEpics();

    void clearEpics();

    int addEpic(Epic epic);

    void removeEpic(int id);

    Epic getEpic(int id);

    int updateEpic(int id, Epic epic);

    List<Subtask> getSubtasksOfEpic(int id);

    //Subtasks
    List<Subtask> getSubtasks();

    int addSubtask(Subtask subtask, Epic epic);

    void removeSubtask(int id);

    Subtask getSubtask(int id);

    int updateSubtask(int id, Subtask subtask);

    void clearSubtasks();

    List<Task> getPrioritizedTasks();

    boolean findIntersection(Task task);
}
