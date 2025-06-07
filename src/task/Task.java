package task;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private Status status;
    private Integer id;

    public Task(String name, String description, Status status) {
        if (name != null && status != null) {
            this.name = name;
            this.description = description;
            this.status = status;
            this.id = hashCode();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status);
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
}
