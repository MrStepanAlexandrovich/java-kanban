package manager;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    static TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    public void beforeEach() {
        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
    }

    @Test
    public void clearTasksMethodShouldClearTasks() {
        for (int i = 0; i < 10; i++) {
            taskManager.addTask(new Task(String.valueOf(i), null, Status.NEW));
        }

        taskManager.clearTasks();
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    public void clearEpicsMethodShouldClearEpics() {
        for (int i = 0; i < 10; i++) {
            taskManager.addEpic(new Epic(String.valueOf(i), null, new ArrayList<>()));
        }

        taskManager.clearEpics();
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    public void clearEpicsMethodShouldClearSubtasks() {
        for (int i = 0; i < 10; i++) {
            taskManager.addEpic(new Epic(String.valueOf(i), null, new ArrayList<>()));
        }

        int i = 0;
        for (Epic epic : taskManager.getEpics().values()) {
            taskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
            taskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
            taskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
        }

        taskManager.clearEpics();
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    public void clearSubtasksMethodShouldClearSubtasks() {
        for (int i = 0; i < 10; i++) {
            taskManager.addEpic(new Epic(String.valueOf(i), null, new ArrayList<>()));
        }

        int i = 0;
        for (Epic epic : taskManager.getEpics().values()) {
            taskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
            taskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
            taskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            i++;
        }

        taskManager.clearSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
    }


    @Test
    public void epicShouldHaveStatusDoneIfAllSubtasksHaveStatusDone() {
        Epic epic = new Epic("asdf", null, new ArrayList<>());
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("asdfvcx", null, Status.DONE, epic));
        taskManager.addSubtask(new Subtask("asdasdffvcx", null, Status.DONE, epic));
        taskManager.addSubtask(new Subtask("asdffvcx", null, Status.DONE, epic));

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void epicShouldHaveStatusNewIfAllSubtasksHaveStatusNew() {
        Epic epic = new Epic("asdf", null, new ArrayList<>());
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("asdfvcx", null, Status.NEW, epic));
        taskManager.addSubtask(new Subtask("asdasdffvcx", null, Status.NEW, epic));
        taskManager.addSubtask(new Subtask("asdffvcx", null, Status.NEW, epic));

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void epicShouldHaveStatusInProgressIfSomeSubtasksHaveStatusDoneSomeStatusInProgressAndSomeStatusNew() {
        Epic epic = new Epic("asdf", null, new ArrayList<>());
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("asdfvcx", null, Status.NEW, epic));
        taskManager.addSubtask(new Subtask("asdasdffvcx", null, Status.DONE, epic));
        taskManager.addSubtask(new Subtask("asdffvcx", null, Status.IN_PROGRESS, epic));

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void removeTaskShouldRemoveTaskFromList() {
        Task task = new Task("asdfas", null, Status.NEW);
        taskManager.addTask(task);
        taskManager.removeTask(task.getId());

        assertNull(taskManager.getTask(task.getId()));
    }
}
