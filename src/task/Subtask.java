package task;

import java.util.Objects;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description, Status status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
        epic.addSubtask(this);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
        epic.addSubtask(this);
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public int hashCode() {
        return 2 * super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epic, subtask.epic);
    }
}
