package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, Status status, Integer epicId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        if (epicId != null) {
            this.epicId = epicId;
            epic.addSubtask(this);
        }
        setType(Type.SUBTASK);
    }

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        if (epic != null) {
            epic.addSubtask(this);
            this.epic = epic;
        }
        setType(Type.SUBTASK);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
        epic.addSubtask(this);
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epic.getId(), subtask.epic.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epic);
    }
}
