package manager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import task.*;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;               //Всё же решил переделать id на счётчик - так гораздо удобней
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

    public void setCounter(int counter) {
        this.counter = counter;
    }

    @Override
    public int addTask(Task task) {
        if (findIntersection(task)) {
            System.out.println("Найдено пересечение!");
            return Integer.MIN_VALUE;
        }

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
        Optional<Task> task = Optional.ofNullable(tasks.get(id));

        if (task.isPresent()) {
            historyManager.add(task.get());
            return task.get();
        } else {
            return null;
        }
    }

    @Override
    public int updateTask(int id, Task task) {
        if (findIntersection(task)) {
            System.out.println("Найдено пересечение!");
            return Integer.MIN_VALUE;
        }

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
        if (findIntersection(epic)) {
            System.out.println("Найдено пересечение!");
            return Integer.MIN_VALUE;
        }

        if (epic != null) {
            epics.put(++counter, epic);
            epic.setId(counter);
        }

        List<Subtask> newSubtasks = epic.getSubtasks();
        for (Subtask subtask : newSubtasks) {
            subtasks.put(subtask.getId(), subtask);    //добавляем сабтаски в список в TaskManager
        }
        refreshStatus(epic);

        return epic.getId();
    }

    public int getCounter() {
        return counter;
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);

        List<Subtask> subtasks = epic.getSubtasks();
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
        Optional<Epic> epic = Optional.ofNullable(epics.get(id));

        if (epic.isPresent()) {
            historyManager.add(epic.get());
            return epic.get();
        } else {
            return null;
        }
    }

    @Override
    public int updateEpic(int id, Epic epic) {
        if (findIntersection(epic)) {
            System.out.println("Найдено пересечение!");
            return Integer.MIN_VALUE;
        }

        List<Subtask> subtasks = getEpic(id).getSubtasks();
        getEpics().set(id, epic);

        refreshStatus(epic);
        epic.refreshTime();
        historyManager.remove(id);
        historyManager.add(epic);

        return epic.getId();
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(Epic epic) {
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
        for (Epic epic : getEpics()) {
            epic.getSubtasks().clear();
            refreshStatus(epic);
        }
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (findIntersection(subtask)) {
            System.out.println("Найдено пересечение!");
            return Integer.MIN_VALUE;
        }

        if (subtask != null) {
            subtasks.put(++counter, subtask);
            subtask.setId(counter);
        }

        subtask.getEpic().getSubtasks().add(subtask);
        refreshStatus(subtask.getEpic());
        subtask.getEpic().refreshTime();

        return subtask.getId();
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = subtask.getEpic();

        epic.getSubtasks().remove(subtask); //Удаляем из эпика
        epic.refreshTime();
        refreshStatus(epic);

        historyManager.remove(id);

        subtasks.remove(id); //Удаляем из списка
    }

    @Override
    public Subtask getSubtask(int id) {
        Optional<Subtask> subtask = Optional.ofNullable(subtasks.get(id));

        if (subtask.isPresent()) {
            historyManager.add(subtask.get());
            return subtask.get();
        } else {
            return null;
        }
    }

    @Override
    public int updateSubtask(int id, Subtask subtask) {
        if (findIntersection(subtask)) {
            System.out.println("Найдено пересечение!");
            return Integer.MIN_VALUE;
        }

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

    public List<Task> getPrioritizedTasks() {
        List<Task> sortedList = new ArrayList<>(getTasks());
        sortedList.addAll(getSubtasks());

        sortedList = sortedList.stream()
                .filter(task -> task.getStartTime() != null)
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());

        return sortedList;
    }

    public boolean findIntersection(Task task) {
        List<Task> sortedList = getPrioritizedTasks();

        return sortedList.stream()
                .anyMatch(taskOfList -> segmentsIntersect(task.getStartTime(), task.getEndTime(),
                        taskOfList.getStartTime(), taskOfList.getEndTime()));
    }

    private boolean segmentsIntersect(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        if (start1 != null && start2 != null && end1 != null && end2 != null) {
            return ((start1.isAfter(start2) || start1.isEqual(start2)) && start1.isBefore(end2))
                    || (end1.isAfter(start2) && (end1.isBefore(end2) || end1.isEqual(end2)));
        } else {
            return false;
        }
    }
}
