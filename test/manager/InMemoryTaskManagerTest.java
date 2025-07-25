package manager;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import task.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    public void afterEach() {
        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
        taskManager.clearHistory();
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
        for (Epic epic : taskManager.getEpics()) {
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
        for (Epic epic : taskManager.getEpics()) {
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

    @Test
    public void tasksShouldntDuplicateInHistory() {
        List<Task> expectedArrayList = new ArrayList<>();

        Task task1 = new Task(String.valueOf(1),
                String.valueOf(2), Status.NEW);
        Task task2 = new Task(String.valueOf(2),
                String.valueOf(3), Status.NEW);
        Task task3 = new Task(String.valueOf(3),
                String.valueOf(4), Status.NEW);

        expectedArrayList.add(task1);
        expectedArrayList.add(task3);
        expectedArrayList.add(task2);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task1.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(history, expectedArrayList);
    }

    @Test
    public void updatedTasksShouldBeUpdatedInHistoryManager() {
        Task task1 = new Task(String.valueOf(1),
                String.valueOf(2), Status.NEW);
        Task task2 = new Task(String.valueOf(2),
                String.valueOf(3), Status.NEW);
        Task task3 = new Task(String.valueOf(3),
                String.valueOf(4), Status.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task1.getId());
        Task task4 = new Task("sadfasdf", "asdfasdf", Status.NEW);

        taskManager.updateTask(task1.getId(), task4);
        assertEquals(taskManager.getHistory().get(0), task4);
    }

    @Test
    public void deletedTaskShouldBeDeletedInHistoryManager() {
        Task task = new Task("asdfa", "asdasdf", Status.NEW);
        Epic epic = new Epic("epic", "asdasdf", new ArrayList<>());
        Subtask subtask = new Subtask("asdfa", "asdasdf", Status.NEW, epic);

        taskManager.addTask(task);
        taskManager.addEpic(epic);

        taskManager.getEpic(epic.getId());
        taskManager.getTask(task.getId());
        taskManager.getSubtask(subtask.getId());

        List<Task> expectedList = new ArrayList<>();
        expectedList.add(subtask);
        expectedList.add(task);
        expectedList.add(epic);

        assertEquals(taskManager.getHistory(), expectedList);
    }

    @Test
    public void addSubtaskInHistory() {
        Epic epic = new Epic("sadfas", "dsasdf", new ArrayList<>());
        Subtask subtask = new Subtask("asda", "asdfas", Status.NEW, epic);
        taskManager.addSubtask(subtask);
        taskManager.getSubtask(subtask.getId());

        assertEquals(taskManager.getHistory().get(0), subtask);
    }

    @Test
    public void EpicShouldntContainDeletedSubtask() {
        int epicId = taskManager.addEpic(new Epic("epic", "asdfasdf", new ArrayList<>()));
        int subtaskId = taskManager.addSubtask(new Subtask("adsfa", "aasdfasdf", Status.NEW, taskManager.getEpic(epicId)));

        assertEquals(taskManager.getEpic(epicId).getSubtasks().size(), 1);

        taskManager.removeSubtask(subtaskId);

        assertEquals(taskManager.getEpic(epicId).getSubtasks().size(), 0);
    }

    @Test
    public void removingEpicShouldRemoveItsSubtasksFromHistory() {
        Epic epic = new Epic("epic", "asdfasdf", new ArrayList<>());
        Subtask subtask1 = new Subtask("subtask1", "asdfas", Status.NEW, epic);
        Subtask subtask2 = new Subtask("subtask2", "asdfas", Status.NEW, epic);
        Subtask subtask3 = new Subtask("subtask3", "asdfas", Status.NEW, epic);

        taskManager.addEpic(epic);

        assertEquals(taskManager.getSubtasks().size(), 3);

        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());

        assertEquals(taskManager.getHistory().size(), 3);

        taskManager.removeEpic(epic.getId());

        assertEquals(taskManager.getHistory().size(), 0);
    }
}
