import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    static InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager.clearTasks();
        inMemoryTaskManager.clearSubtasks();
        inMemoryTaskManager.clearEpics();
    }

    @Test
    public void clearTasksMethodShouldClearTasks() {
        for (int i = 0; i < 10; i++) {
            inMemoryTaskManager.addTask(new Task(String.valueOf(i), null, Status.NEW));
        }

        inMemoryTaskManager.clearTasks();
        assertTrue(inMemoryTaskManager.getTasks().isEmpty());
    }

    @Test
    public void clearEpicsMethodShouldClearEpics() {
        for (int i = 0; i < 10; i++) {
            inMemoryTaskManager.addEpic(new Epic(String.valueOf(i), null, new ArrayList<>()));
        }

        inMemoryTaskManager.clearEpics();
        assertTrue(inMemoryTaskManager.getEpics().isEmpty());
    }

    @Test
    public void clearEpicsMethodShouldClearSubtasks() {
        for (int i = 0; i < 10; i++) {
            inMemoryTaskManager.addEpic(new Epic(String.valueOf(i), null, new ArrayList<>()));
        }

        int i = 0;
        for (Epic epic : inMemoryTaskManager.getEpics().values()) {
            inMemoryTaskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
            inMemoryTaskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
            inMemoryTaskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
        }

        inMemoryTaskManager.clearEpics();
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty());
    }

    @Test
    public void clearSubtasksMethodShouldClearSubtasks() {
        for (int i = 0; i < 10; i++) {
            inMemoryTaskManager.addEpic(new Epic(String.valueOf(i), null, new ArrayList<>()));
        }

        int i = 0;
        for (Epic epic : inMemoryTaskManager.getEpics().values()) {
            inMemoryTaskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
            inMemoryTaskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
            inMemoryTaskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
        }

        inMemoryTaskManager.clearSubtasks();
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty());
    }


    @Test
    public void epicShouldHaveStatusDoneIfAllSubtasksHaveStatusDone() {
        Epic epic = new Epic("asdf", null, new ArrayList<>());
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(new Subtask("asdfvcx", null, Status.DONE, epic));
        inMemoryTaskManager.addSubtask(new Subtask("asdasdffvcx", null, Status.DONE, epic));
        inMemoryTaskManager.addSubtask(new Subtask("asdffvcx", null, Status.DONE, epic));

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void epicShouldHaveStatusNewIfAllSubtasksHaveStatusNew() {
        Epic epic = new Epic("asdf", null, new ArrayList<>());
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(new Subtask("asdfvcx", null, Status.NEW, epic));
        inMemoryTaskManager.addSubtask(new Subtask("asdasdffvcx", null, Status.NEW, epic));
        inMemoryTaskManager.addSubtask(new Subtask("asdffvcx", null, Status.NEW, epic));

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void epicShouldHaveStatusInProgressIfSomeSubtasksHaveStatusDoneSomeStatusInProgressAndSomeStatusNew() {
        Epic epic = new Epic("asdf", null, new ArrayList<>());
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(new Subtask("asdfvcx", null, Status.NEW, epic));
        inMemoryTaskManager.addSubtask(new Subtask("asdasdffvcx", null, Status.DONE, epic));
        inMemoryTaskManager.addSubtask(new Subtask("asdffvcx", null, Status.IN_PROGRESS, epic));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void removeTaskShouldRemoveTaskFromList() {
        Task task = new Task("asdfas", null, Status.NEW);
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.removeTask(task.getId());

        assertNull(inMemoryTaskManager.getTask(task.getId()));
    }
}
