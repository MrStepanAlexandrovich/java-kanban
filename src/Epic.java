import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void refreshStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
        } else {
            ArrayList<Status> subtaskStatusList = new ArrayList<>();
            for (Subtask subtask : subtasks) {
                subtaskStatusList.add(subtask.getStatus());
            }

            if (!subtaskStatusList.contains(Status.DONE) && !subtaskStatusList.contains(Status.IN_PROGRESS)) {
                setStatus(Status.NEW);
            } else if (!subtaskStatusList.contains(Status.NEW) && !subtaskStatusList.contains(Status.IN_PROGRESS)) {
                setStatus(Status.DONE);
            } else {
                setStatus(Status.IN_PROGRESS);
            }
        }
    }
}
