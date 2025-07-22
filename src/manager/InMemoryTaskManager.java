package manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import task.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Tasks
    @Override
    public List<Task> getTasks() {
        return collectionToList(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public int addTask(Task task) {
        if (task != null) {
            tasks.put(task.hashCode(), task);
        }

        return task.getId();
    }

    @Override
    public void removeTask(int hash) {
        historyManager.remove(hash);
        tasks.remove(hash);
    }

    @Override
    public Task getTask(int hash) {
        Task task = tasks.get(hash);
        historyManager.add(task);
        return task;
    }

    @Override
    public int updateTask(int oldTaskHash, Task task) {
        tasks.put(task.getId(), task);
        tasks.remove(oldTaskHash);

        historyManager.remove(oldTaskHash);
        historyManager.add(task);

        return task.getId();
    }


    //Epics
    @Override
    public List<Epic> getEpics() {
        return collectionToList(epics.values());
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.hashCode(), epic);
        }

        ArrayList<Subtask> newSubtasks = epic.getSubtasks();
        for (Subtask subtask : newSubtasks) {
            subtasks.put(subtask.getId(), subtask);    //добавляем сабтаски в список в TaskManager
        }
        refreshStatus(epic);

        return epic.getId();
    }

    @Override
    public void removeEpic(int hash) {
        Epic epic = epics.get(hash);

        ArrayList<Subtask> subtasks = epic.getSubtasks();
        for (Subtask subtask : subtasks) {
            historyManager.remove(subtask.getId());
            this.subtasks.remove(subtask.getId()); //Удаляем сабтаски эпика из списка в TaskManager
        }

        historyManager.remove(hash);
        epic.getSubtasks().clear(); //Удаляем сабтаски из объекта

        epics.remove(hash); //Удаляем эпик
    }

    @Override
    public Epic getEpic(int hash) {
        Epic epic = epics.get(hash);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public int updateEpic(int oldEpicHash, Epic epic) {
        removeEpic(oldEpicHash);
        List<Subtask> subtasks = getEpic(oldEpicHash).getSubtasks();
        addEpic(epic);
        for (Subtask subtask : subtasks) {
            subtask.setEpic(epic);
        }
        refreshStatus(epic);
        historyManager.add(epic);
        historyManager.remove(oldEpicHash);

        return epic.getId();
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        return epic.getSubtasks();
    }

    public void refreshStatus(Epic epic) {
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
    @Override
    public List<Subtask> getSubtasks() {
        return collectionToList(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            refreshStatus(epic);
        }
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.hashCode(), subtask);
        }
        Epic epic = subtask.getEpic();
        epic.getSubtasks().add(subtask);
        refreshStatus(epic);

        return subtask.getId();
    }

    @Override
    public void removeSubtask(int hash) {
        Subtask subtask = subtasks.get(hash);
        Epic epic = subtask.getEpic();

        historyManager.remove(hash);
        epic.getSubtasks().remove(subtask); //Удаляем из эпика
        refreshStatus(epic);

        subtasks.remove(hash); //Удаляем из списка
    }

    @Override
    public Subtask getSubtask(int hash) {
        Subtask subtask = subtasks.get(hash);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int updateSubtask(int oldSubtaskHash, Subtask subtask) {
        addSubtask(subtask);

        Subtask oldSubtask = getSubtask(oldSubtaskHash);
        Epic epicOfOldSubtask = oldSubtask.getEpic();
        subtask.setEpic(epicOfOldSubtask);

        removeSubtask(oldSubtaskHash);
        refreshStatus(epicOfOldSubtask);
        refreshStatus(subtask.getEpic());

        historyManager.add(subtask);
        historyManager.remove(oldSubtaskHash);

        return subtask.getId();
    }

    @Override
    public void clearHistory() {
        for (Task task : historyManager.getHistory()) {
            int id = task.getId();
            historyManager.remove(id);
        }
    }

    private <T> List<T> collectionToList(Collection<T> collection) {
        List<T> list = new ArrayList<>();
        list.addAll(collection);
        return list;
    }
}
