package task;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
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
        Epic epic = new Epic("A", "s");
        Subtask subtask1 = new Subtask("a", "a", Status.DONE, epic);
        Subtask subtask2 = new Subtask("a", "a", Status.DONE, epic);
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void EqualEpicsShouldBeEqual() {
        Epic epic1 = new Epic("A", "s");
        Epic epic2 = new Epic("A", "s");
        Subtask subtask1 = new Subtask("a", "a", Status.DONE, epic1);
        Subtask subtask2 = new Subtask("a", "a", Status.DONE, epic2);
        epic1.equals(epic2);
    }

    @Test
    public void EpicShouldCalculateStartDurationEndDependingOnSubtasks() {
        Epic epic = new Epic("epic", null);
        Subtask subtask = null;
        LocalDateTime time = LocalDateTime.of(2000, 11, 21, 10, 50);
        for (int i = 0; i < 10; i++) {
            time = time.plusMinutes(10 * i);
            subtask = new Subtask("subtask" + i, null, Status.DONE, epic,
                    time, Duration.ofMinutes(10 * i));
        }

        assertEquals(LocalDateTime.of(2000, 11, 21, 10, 50), epic.getStartTime());

        assertEquals(subtask.getEndTime(), epic.getEndTime());
    }
}
