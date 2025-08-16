package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        setType(Type.EPIC);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        refreshTime();
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
        Optional<Subtask> firstSubtask = subtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(Comparator.comparing(subtask -> subtask.getStartTime()));

        if (firstSubtask.isPresent()) {
            return firstSubtask.get().getStartTime();
        } else {
            return null;
        }
    }

    private LocalDateTime findEndTime() {
        Optional<Subtask> lastSubtask = subtasks.stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(subtask -> subtask.getEndTime()));

        if (lastSubtask.isPresent()) {
            return lastSubtask.get().getEndTime();
        } else {
            return null;
        }
    }

    private Duration calculateDuration() {
        if (getStartTime() != null && getEndTime() != null) {
            return Duration.between(getStartTime(), getEndTime());
        } else {
            return null;
        }
    }

    public void refreshTime() {
        setStartTime(findStartTime());
        setEndTime(findEndTime());
        setDuration(calculateDuration());
    }

    private void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
