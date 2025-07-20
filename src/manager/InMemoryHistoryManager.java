package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node first;
    private Node last;
    private final Map<Integer, Node> history = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId());
            Node node = new Node(task);
            linkLast(node);
            history.put(task.getId(), node);
        }
    }

    @Override
    public void clear() {
        for (Node node : history.values()) {
            node.setPrevious(null);
            node.setData(null);
            node.setNext(null);
            node = null;
        }
        history.clear();
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
        history.remove(id);
        if (history.size() == 0) {
            first = null;
            last = null;
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void updateTask(int oldTaskId, Task task) {
        var node = history.get(oldTaskId);
        node.setData(task);
        history.put(task.getId(), node);
        history.remove(oldTaskId);
    }

    //Методы связного списка
    private void linkLast(Node node) {
        if (first == null && last == null) {
            first = node;
            last = node;
        } else if (last == first && last != null) {
            node.setPrevious(first);
            first.setNext(node);
            last = node;
        } else {
            node.setPrevious(last);
            last.setNext(node);
            last = node;
        }
    }

    private void removeNode(Node node) {
        if (node != null) {
            Node next = node.getNext();
            Node previous = node.getPrevious();
            if (next == null && previous != null) { //Если удаляем последний элемент
                previous.setNext(null);
                last = previous;
            } else if (next != null && previous == null) { //Если удаляем первый элемент
                next.setPrevious(null);
                first = next;
            } else if (next != null && previous != null) {
                previous.setNext(next);
                next.setPrevious(previous);
            }
        }
    }

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node node = last;
        while (node != null) {
            taskList.add(node.getData());
            node = node.getPrevious();
        }

        return taskList;
    }
    //
}
