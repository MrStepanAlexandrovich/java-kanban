package task;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TasksEpicsSubtasksTest {
    @Test
    public void EqualTasksShouldBeEqual() {
        Task task1 = new Task("a", "a", Status.DONE);
        Task task2 = new Task("a", "a", Status.DONE);
        assertEquals(task1, task2);
    }

    @Test
    public void EqualSubtasksShouldBeEqual() {
        Epic epic = new Epic("A", "s", new ArrayList<>());
        Subtask subtask1 = new Subtask("a", "a", Status.DONE, epic);
        Subtask subtask2 = new Subtask("a", "a", Status.DONE, epic);
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void EqualEpicsShouldBeEqual() {
        Epic epic1 = new Epic("A", "s", new ArrayList<>());
        Epic epic2 = new Epic("A", "s", new ArrayList<>());
        Subtask subtask1 = new Subtask("a", "a", Status.DONE, epic1);
        Subtask subtask2 = new Subtask("a", "a", Status.DONE, epic2);
        epic1.equals(epic2);
    }
}
