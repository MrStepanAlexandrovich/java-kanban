package manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import task.*;

public class InMemoryTaskManager implements TaskManager {
    private static int counter = 0;
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
            tasks.put(++counter, task);
            task.setId(counter);
        }

        return task.getId();
    }

    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public int updateTask(int id, Task task) {
        tasks.put(id, task);
        task.setId(id);
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
            epics.put(++counter, epic);
            epic.setId(counter);
        }

        ArrayList<Subtask> newSubtasks = epic.getSubtasks();
        for (Subtask subtask : newSubtasks) {
            subtasks.put(subtask.getId(), subtask);    //добавляем сабтаски в список в TaskManager
        }
        refreshStatus(epic);

        return epic.getId();
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);

        ArrayList<Subtask> subtasks = epic.getSubtasks();
        for (Subtask subtask : subtasks) {
            historyManager.remove(subtask.getId());
            this.subtasks.remove(subtask.getId()); //Удаляем сабтаски эпика из списка в TaskManager
        }

        historyManager.remove(id);
        epic.getSubtasks().clear(); //Удаляем сабтаски из объекта

        epics.remove(id); //Удаляем эпик
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public int updateEpic(int id, Epic epic) {
        List<Subtask> subtasks = getEpic(id).getSubtasks();
        getEpics().set(id, epic);

        refreshStatus(epic);
        historyManager.remove(id);
        historyManager.add(epic);

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
            subtasks.put(++counter, subtask);
            subtask.setId(counter);
        }
        Epic epic = subtask.getEpic();
        epic.getSubtasks().add(subtask);
        refreshStatus(epic);

        return subtask.getId();
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = subtask.getEpic();

        historyManager.remove(id);
        epic.getSubtasks().remove(subtask); //Удаляем из эпика
        refreshStatus(epic);

        subtasks.remove(id); //Удаляем из списка
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int updateSubtask(int id, Subtask subtask) {
        getSubtasks().set(id, subtask);
        historyManager.remove(id);
        historyManager.add(subtask);

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
