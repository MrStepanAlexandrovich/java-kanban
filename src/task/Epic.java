package task;

import manager.TaskManager;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description, ArrayList<Subtask> subtasks) {
        super(name, description, Status.NEW);
        this.subtasks = subtasks;
        TaskManager.refreshStatus(this);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }
}
