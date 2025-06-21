package manager;

import task.Task;

import java.util.ArrayList;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();
    private static final int HISTORY_SIZE = 10;

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() < HISTORY_SIZE) {
                history.add(task);
            } else {
                history.remove(0);
                history.add(task);
            }
        }
    }
}
