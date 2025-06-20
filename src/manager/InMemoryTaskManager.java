package manager;

import java.util.ArrayList;
import java.util.HashMap;
import task.*;

public class InMemoryTaskManager implements TaskManager{
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Tasks
    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            tasks.put(task.hashCode(), task);
        }
    }

    @Override
    public void removeTask(int hash) {
        tasks.remove(hash);
    }

    @Override
    public Task getTask(int hash) {
        Task task = tasks.get(hash);
        historyManager.add(task);
        return task;
    }

    @Override
    public void updateTask(int oldTaskHash, Task task) {
        tasks.put(task.getId(), task);
        tasks.remove(oldTaskHash);
    }


    //Epics
    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
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
    public void addEpic(Epic epic) {
        if (epic != null) {
            epics.put(epic.hashCode(), epic);
        }

        ArrayList<Subtask> newSubtasks = epic.getSubtasks();
        for (Subtask subtask : newSubtasks) {
            subtasks.put(subtask.getId(), subtask);    //добавляем сабтаски в список в TaskManager
        }
        refreshStatus(epic);
    }

    @Override
    public void removeEpic(int hash) {
        Epic epic = epics.get(hash);

        ArrayList<Subtask> subtasks = epic.getSubtasks();
        for (Subtask subtask : subtasks) {
            this.subtasks.remove(subtask.getId()); //Удаляем сабтаски эпика из списка в TaskManager
        }

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
    public void updateEpic(int oldEpicHash, Epic epic) {
        removeEpic(oldEpicHash);
        addEpic(epic);
        refreshStatus(epic);
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
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
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
    public void addSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.hashCode(), subtask);
        }
        Epic epic = subtask.getEpic();
        epic.getSubtasks().add(subtask);
        refreshStatus(epic);
    }

    @Override
    public void removeSubtask(int hash) {
        Subtask subtask = subtasks.get(hash);
        Epic epic = subtask.getEpic();

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
    public void updateSubtask(int oldSubtaskHash, Subtask subtask) {
        addSubtask(subtask);

        Subtask oldSubtask = getSubtask(oldSubtaskHash);
        Epic epicOfOldSubtask = oldSubtask.getEpic();

        removeSubtask(oldSubtaskHash);
        refreshStatus(epicOfOldSubtask);
        refreshStatus(subtask.getEpic());
    }
}
