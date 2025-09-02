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
        if (task != null) {
            if (findIntersection(task)) {
                System.out.println("Найдено пересечение!");
                return Integer.MIN_VALUE;
            }
            tasks.put(++counter, task);
            task.setId(counter);
            return task.getId();
        } else {
            return Integer.MIN_VALUE;
        }
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
        if (tasks.containsKey(id)) {
            if (task != null) {
                if (findIntersection(task)) {
                    System.out.println("Найдено пересечение!");
                    return Integer.MIN_VALUE;
                } else {
                    tasks.put(id, task);
                    task.setId(id);
                    historyManager.add(task);

                    return task.getId();
                }
            } else {
                System.out.println("Задача не была изменена!");
                return Integer.MIN_VALUE;
            }
        } else {
            System.out.println("Некорректный ID!");
            return Integer.MIN_VALUE;
        }
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
            if (findIntersection(epic)) {
                System.out.println("Найдено пересечение!");
                return Integer.MIN_VALUE;
            }

            if (epic != null) {
                epics.put(++counter, epic);
                epic.setId(counter);
            }

            return epic.getId();
        } else {
            System.out.println("Эпик не был добавлен!");
            return Integer.MIN_VALUE;
        }
    }

    public int getCounter() {
        return counter;
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);

            List<Subtask> subtasks = epic.getSubtasks();
            for (Subtask subtask : subtasks) {
                historyManager.remove(subtask.getId());
                this.subtasks.remove(subtask.getId()); //Удаляем сабтаски эпика из списка в TaskManager
            }

            historyManager.remove(id);
            epic.getSubtasks().clear(); //Удаляем сабтаски из объекта

            epics.remove(id); //Удаляем эпик
        } else {
            System.out.println("Некорректный ID!");
        }
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
        if (epics.containsKey(id)) {
            if (epic != null) {
                if (findIntersection(epic)) {
                    System.out.println("Найдено пересечение!");
                    return Integer.MIN_VALUE;
                }

                epics.replace(id, epic);

                epic.setId(id);

                historyManager.remove(id);
                historyManager.add(epic);

                return epic.getId();
            } else {
                System.out.println("Эпик не был обновлен!");
                return Integer.MIN_VALUE;
            }
        } else {
            System.out.println("Некорректный ID!");
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int id) {
        return epics.get(id).getSubtasks();
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
    public int addSubtask(Subtask subtask, Epic epic) {
        if (!subtasks.containsValue(subtask)) {
            if (findIntersection(subtask)) {
                System.out.println("Найдено пересечение!");
                return Integer.MIN_VALUE;
            }

            if (subtask != null) {
                subtasks.put(++counter, subtask);
                subtask.setId(counter);

                epic.addSubtask(subtask);
                subtask.setEpicId(epic.getId());
                refreshStatus(epic);
                epic.refreshTime();

                return subtask.getId();
            } else {
                return Integer.MIN_VALUE;
            }
        } else {
            System.out.println("Подзадача уже есть в списке!");
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());

            epic.getSubtasks().remove(subtask); //Удаляем из эпика
            epic.refreshTime();
            refreshStatus(epic);

            historyManager.remove(id);

            subtasks.remove(id); //Удаляем из списка
        } else {
            System.out.println("Некорректный ID!");
        }
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
        if (subtasks.containsKey(id)) {
            if (subtask != null) {
                if (findIntersection(subtask)) {
                    System.out.println("Найдено пересечение!");
                    return Integer.MIN_VALUE;
                }

                subtasks.replace(id, subtask);
                subtask.setId(id);
                historyManager.remove(id);
                historyManager.add(subtask);

                return subtask.getId();
            } else {
                System.out.println("Подзадача не была обновлена!");
                return Integer.MIN_VALUE;
            }
        } else {
            System.out.println("Некорректный ID!");
            return Integer.MIN_VALUE;
        }
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
                .anyMatch(taskOfList -> segmentsIntersect(task, taskOfList));
    }

    private boolean segmentsIntersect(Task task1, Task task2) {
        LocalDateTime task1StartTime = task1.getStartTime();
        LocalDateTime task1EndTime = task1.getEndTime();
        LocalDateTime task2StartTime = task2.getStartTime();
        LocalDateTime task2EndTime = task2.getEndTime();

        if (task1StartTime != null && task2StartTime != null && task1EndTime != null && task2EndTime != null) {
            return ((task1StartTime.isAfter(task2StartTime) || task1StartTime.isEqual(task2StartTime)) && task1StartTime.isBefore(task2EndTime))
                    || (task1EndTime.isAfter(task2StartTime) && (task1EndTime.isBefore(task2EndTime) || task1EndTime.isEqual(task2EndTime)));
        } else {
            return false;
        }
    }
}
