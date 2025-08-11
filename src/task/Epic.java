package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, ArrayList<Subtask> subtasks, LocalDateTime startTime,
                LocalDateTime endTime) {
        super(name, description, Status.NEW);
        this.subtasks = subtasks;
        setType(Type.EPIC);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    private LocalDateTime findStartTime() {
        return subtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(subtask -> subtask.getStartTime()))
                .get()
                .getStartTime();
    }

    private LocalDateTime findEndTime() {
        return subtasks.stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(subtask -> subtask.getEndTime()))
                .get()
                .getEndTime();
    }

    private Duration calculateDuration() {
        return Duration.between(getStartTime(), getEndTime());
    }

    private List<Subtask> getSortedSubtasks() {
        List<Subtask> sortedList = List.copyOf(subtasks);
        sortedList.sort(Comparator.comparing(Task::getStartTime));
        return sortedList;
    }


    private void findIntersection() {
        List<Subtask> sortedList = getSortedSubtasks();

        sortedList.stream().
    }
}
