package manager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = createTaskManager();
    }

    @Test
    public void epicStatusChangingTest() {
        Epic epic = new Epic("epic", null);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", null, Status.NEW, epic,
                LocalDateTime.of(2020, 11, 21, 21, 59), Duration.ofMinutes(60));
        Subtask subtask2 = new Subtask("subtask2", null, Status.NEW, epic,
                LocalDateTime.of(2021, 11, 21, 21, 59), Duration.ofMinutes(60));

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus());

        taskManager.clearSubtasks();
        assertEquals(0, epic.getSubtasks().size());

        Subtask subtask3 = new Subtask("subtask3", null, Status.DONE, epic,
                LocalDateTime.of(2022, 11, 21, 21, 59), Duration.ofMinutes(60));
        Subtask subtask4 = new Subtask("subtask4", null, Status.DONE, epic,
                LocalDateTime.of(2017, 11, 21, 21, 59), Duration.ofMinutes(60));

        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);

        assertEquals(Status.DONE, epic.getStatus());

        taskManager.clearSubtasks();
        assertEquals(0, epic.getSubtasks().size());

        Subtask subtask5 = new Subtask("subtask5", null, Status.NEW, epic,
                LocalDateTime.of(2005, 11, 21, 21, 59), Duration.ofMinutes(60));
        Subtask subtask6 = new Subtask("subtask6", null, Status.DONE, epic,
                LocalDateTime.of(2006, 11, 21, 21, 59), Duration.ofMinutes(60));

        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        taskManager.clearSubtasks();

        Subtask subtask7 = new Subtask("subtask7", null, Status.IN_PROGRESS, epic,
                LocalDateTime.of(2005, 11, 21, 21, 59), Duration.ofMinutes(60));
        Subtask subtask8 = new Subtask("subtask8", null, Status.IN_PROGRESS, epic,
                LocalDateTime.of(2006, 11, 21, 21, 59), Duration.ofMinutes(60));

        taskManager.addSubtask(subtask7);
        taskManager.addSubtask(subtask8);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void intersectionsFindings() {
        Task task1 = taskManager.getTask(taskManager.addTask(new Task("abc1", null, Status.DONE,
                LocalDateTime.of(2020, 11, 21, 20, 59), Duration.ofMinutes(110))));
        Task task2 = taskManager.getTask(taskManager.addTask(new Task("abc2", null, Status.DONE,
                LocalDateTime.of(2020, 11, 21, 22, 59), Duration.ofMinutes(90))));
        Task task3 = taskManager.getTask(taskManager.addTask(new Task("abc3", null, Status.DONE,
                LocalDateTime.of(2024, 11, 21, 22, 59), Duration.ofMinutes(90))));
        Task task4 = taskManager.getTask(taskManager.addTask(new Task("abc4", null, Status.DONE,
                LocalDateTime.of(2019, 11, 21, 22, 59), Duration.ofMinutes(90))));
        Task task5 = taskManager.getTask(taskManager.addTask(new Task("abc5", null, Status.DONE,
                LocalDateTime.of(2004, 11, 21, 22, 59), Duration.ofMinutes(1))));

        Task task6 = new Task("abc6", null, Status.DONE,
                LocalDateTime.of(2004, 11, 21, 22, 58), Duration.ofMinutes(1));
        Task task7 = new Task("abc7", null, Status.DONE,
                LocalDateTime.of(2004, 11, 21, 23, 00), Duration.ofMinutes(90));
        Task task8 = new Task("abc8", null, Status.DONE,
                LocalDateTime.of(2004, 11, 21, 22, 59), Duration.ofMinutes(1));

        taskManager.addTask(task8);

        assertFalse(taskManager.findIntersection(task6));
        assertFalse(taskManager.findIntersection(task7));
        assertTrue(taskManager.findIntersection(task8));
        assertEquals(5, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void taskManagerShouldReturnPrioritizedTaskList() {
        Task task1 = taskManager.getTask(taskManager.addTask(new Task("abc1", null, Status.DONE,
                LocalDateTime.of(2020, 11, 21, 22, 59), Duration.ofMinutes(90))));
        Task task2 = taskManager.getTask(taskManager.addTask(new Task("abc2", null, Status.DONE,
                LocalDateTime.of(2021, 11, 21, 22, 59), Duration.ofMinutes(90))));
        Task task3 = taskManager.getTask(taskManager.addTask(new Task("abc3", null, Status.DONE,
                LocalDateTime.of(2024, 11, 21, 22, 59), Duration.ofMinutes(90))));
        Task task4 = taskManager.getTask(taskManager.addTask(new Task("abc4", null, Status.DONE,
                LocalDateTime.of(2019, 11, 21, 22, 59), Duration.ofMinutes(90))));
        Task task5 = taskManager.getTask(taskManager.addTask(new Task("abc5", null, Status.DONE,
                LocalDateTime.of(2004, 11, 21, 22, 59), Duration.ofMinutes(90))));

        List<Task> expectedList = new ArrayList<>();

        expectedList.add(task5);
        expectedList.add(task4);
        expectedList.add(task1);
        expectedList.add(task2);
        expectedList.add(task3);

        assertEquals(expectedList, taskManager.getPrioritizedTasks());
    }


    @Test
    public void removingEpicShouldRemoveItsSubtasksFromHistory() {
        int epicId = taskManager.addEpic(new Epic("epic", "asdfasdf"));
        int subtaskId1 = taskManager.addSubtask(new Subtask("subtask1", "asdfas",
                Status.NEW, taskManager.getEpic(epicId)));
        int subtaskId2 = taskManager.addSubtask(new Subtask("subtask2", "asdfas", Status.NEW, taskManager.getEpic(epicId)));
        int subtaskId3 = taskManager.addSubtask(new Subtask("subtask3", "asdfas", Status.NEW, taskManager.getEpic(epicId)));

        assertEquals(taskManager.getSubtasks().size(), 3);

        taskManager.getSubtask(subtaskId1);
        taskManager.getSubtask(subtaskId2);
        taskManager.getSubtask(subtaskId3);

        assertEquals(taskManager.getHistory().size(), 4);

        taskManager.removeEpic(epicId);

        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test
    public void addSubtaskInHistory() {
        Epic epic = new Epic("sadfas", "dsasdf");
        Subtask subtask = new Subtask("asda", "asdfas", Status.NEW, epic);
        taskManager.addSubtask(subtask);
        taskManager.getSubtask(subtask.getId());

        assertEquals(taskManager.getHistory().get(0), subtask);
    }

    @Test
    public void deletedTaskShouldBeDeletedInHistoryManager() {
        int taskId = taskManager.addTask(new Task("asdfa", "asdasdf", Status.NEW));
        int epicId = taskManager.addEpic(new Epic("epic", "asdasdf"));
        int subtaskId = taskManager.addSubtask(new Subtask("sub", "asdasd", Status.DONE, taskManager.getEpic(epicId)));

        taskManager.getEpic(epicId);
        taskManager.getTask(taskId);
        taskManager.getSubtask(subtaskId);

        assertEquals(3, taskManager.getHistory().size());

        taskManager.removeTask(taskId);

        assertEquals(2, taskManager.getHistory().size());

        taskManager.removeEpic(epicId);

        assertEquals(0, taskManager.getHistory().size());
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
    public void clearEpicsMethodShouldClearSubtasks() {
        for (int i = 0; i < 10; i++) {
            taskManager.addEpic(new Epic(String.valueOf(i), null));
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
    public void clearEpicsMethodShouldClearEpicsAndSubtasks() {
        for (int i = 0; i < 10; i++) {
            Epic epic = taskManager.getEpic(taskManager.addEpic(new Epic(String.valueOf(i), null)));
            taskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW, epic));
            taskManager.addSubtask(new Subtask(String.valueOf(i + 35), null, Status.NEW, epic));
            taskManager.addSubtask(new Subtask(String.valueOf(i + 78), null, Status.NEW, epic));
        }

        taskManager.clearEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }
}
