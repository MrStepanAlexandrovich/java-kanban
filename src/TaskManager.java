import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static final HashMap<Integer, Task> tasks = new HashMap<>();

    public static HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public static void clear() {
        tasks.clear();
    }

    public static void addTask(Task task) {
        if (task != null) {
            tasks.put(task.hashCode(), task);
        }
    }

    public static void addSubtaskInEpic(Epic epic, Subtask subtask) {
        if (epic != null && subtask != null) {
            ArrayList<Subtask> subtasks = epic.getSubtasks();
            subtasks.add(subtask);
            subtask.setEpic(epic);
            epic.refreshStatus();
        }
    }

    public static void removeTask(int hash) {
        tasks.remove(hash);
    }

    public static Task getTask(int hash) {
        return tasks.get(hash);
    }

    public static void update(int OldTaskHash, Task task) {
        tasks.put(task.getId(), task);
        tasks.remove(OldTaskHash);
    }
}
