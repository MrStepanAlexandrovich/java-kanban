package task;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TasksEpicsSubtasksTest {
    @Test
    public void equalTasksShouldBeEqual() {
        Task task1 = new Task("a", "a", Status.DONE);
        Task task2 = new Task("a", "a", Status.DONE);
        assertEquals(task1, task2);
    }

    @Test
    public void equalSubtasksShouldBeEqual() {
        Epic epic = new Epic("A", "s");
        Subtask subtask1 = new Subtask("a", "a", Status.DONE, epic);
        Subtask subtask2 = new Subtask("a", "a", Status.DONE, epic);
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void equalEpicsShouldBeEqual() {
        Epic epic1 = new Epic("A", "s");
        Epic epic2 = new Epic("A", "s");
        Subtask subtask1 = new Subtask("a", "a", Status.DONE, epic1);
        Subtask subtask2 = new Subtask("a", "a", Status.DONE, epic2);
        epic1.equals(epic2);
    }

    @Test
    public void epicShouldCalculateStartDurationEndDependingOnSubtasks() {
        Epic epic = new Epic("epic", null);
        Subtask subtask = null;
        LocalDateTime time = LocalDateTime.of(2000, 11, 21, 10, 50);
        Subtask subtask1 = new Subtask("sub1", null, Status.DONE, epic,
                LocalDateTime.of(2022, 11, 21, 21, 21), Duration.ofMinutes(60));
        Subtask subtask2 = new Subtask("sub2", null, Status.DONE, epic,
                LocalDateTime.of(2022, 11, 22, 21, 21), Duration.ofMinutes(60));
        Subtask subtask3 = new Subtask("sub3", null, Status.DONE, epic,
                LocalDateTime.of(2022, 11, 23, 21, 21), Duration.ofMinutes(60));

        assertEquals(subtask1.getStartTime(), epic.getStartTime());
        assertEquals(subtask1.getDuration().plus(subtask2.getDuration().plus(subtask3.getDuration())),
                epic.getDuration());
        assertEquals(subtask3.getEndTime(), epic.getEndTime());
    }
}
