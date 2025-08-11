package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Status status;
    private Integer id;
    private Type type;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        if (name != null && status != null) {
            this.name = name;
            this.description = description;
            this.status = status;
            this.id = null;
            this.type = Type.TASK;
            this.startTime = startTime;
            this.duration = duration;
        }
    }

    public Task(String name, String description, Status status) {
        if (name != null && status != null) {
            this.name = name;
            this.description = description;
            this.status = status;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && Objects.equals(id, task.id) && type == task.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id, type);
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getId() {
        return id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setId(Integer id) {
        if (id >= 1) {
            this.id = id;
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
