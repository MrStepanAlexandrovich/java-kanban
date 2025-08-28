package task;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TasksEpicsSubtasksTest {
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void equalTasksShouldBeEqual() {
        Task task1 = new Task("a", "a", Status.DONE);
        Task task2 = new Task("a", "a", Status.DONE);
        assertEquals(task1, task2);
    }

    @Test
    public void equalSubtasksShouldBeEqual() {
        Subtask subtask1 = new Subtask("a", "a", Status.DONE);
        Subtask subtask2 = new Subtask("a", "a", Status.DONE);

        assertEquals(subtask1, subtask2);
    }

    @Test
    public void equalEpicsShouldBeEqual() {
        Epic epic1 = new Epic("A", "s");
        Epic epic2 = new Epic("A", "s");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("a", "a", Status.DONE);
        Subtask subtask2 = new Subtask("a", "a", Status.DONE);

        taskManager.addSubtask(subtask1, epic1);
        taskManager.addSubtask(subtask2, epic2);

        epic1.equals(epic2);
    }

    @Test
    public void epicShouldCalculateStartDurationEndDependingOnSubtasks() {
        Epic epic = new Epic("epic", null);
        Subtask subtask = null;
        LocalDateTime time = LocalDateTime.of(2000, 11, 21, 10, 50);
        Subtask subtask1 = new Subtask("sub1", null, Status.DONE,
                LocalDateTime.of(2022, 11, 21, 21, 21), Duration.ofMinutes(60));
        Subtask subtask2 = new Subtask("sub2", null, Status.DONE,
                LocalDateTime.of(2022, 11, 22, 21, 21), Duration.ofMinutes(60));
        Subtask subtask3 = new Subtask("sub3", null, Status.DONE,
                LocalDateTime.of(2022, 11, 23, 21, 21), Duration.ofMinutes(60));

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1, epic);
        taskManager.addSubtask(subtask2, epic);
        taskManager.addSubtask(subtask3, epic);

        assertEquals(subtask1.getStartTime(), epic.getStartTime());
        assertEquals(subtask1.getDuration().plus(subtask2.getDuration().plus(subtask3.getDuration())),
                epic.getDuration());
        assertEquals(subtask3.getEndTime(), epic.getEndTime());
    }
}
