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

public abstract class TaskManagerTest<T extends TaskManager> {
    private T taskManager;

    public abstract T createTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = createTaskManager();
    }

    @Test
    public void epicStatusChangingTest() {
        Epic epic = new Epic("epic", null);
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", null, Status.NEW,
                LocalDateTime.of(2020, 11, 21, 21, 59), Duration.ofMinutes(60));
        Subtask subtask2 = new Subtask("subtask2", null, Status.NEW,
                LocalDateTime.of(2021, 11, 21, 21, 59), Duration.ofMinutes(60));

        taskManager.addSubtask(subtask1, epic);
        taskManager.addSubtask(subtask2, epic);

        assertEquals(Status.NEW, epic.getStatus());

        taskManager.clearSubtasks();
        assertEquals(0, epic.getSubtasks().size());

        Subtask subtask3 = new Subtask("subtask3", null, Status.DONE,
                LocalDateTime.of(2022, 11, 21, 21, 59), Duration.ofMinutes(60));
        Subtask subtask4 = new Subtask("subtask4", null, Status.DONE,
                LocalDateTime.of(2017, 11, 21, 21, 59), Duration.ofMinutes(60));

        taskManager.addSubtask(subtask3, epic);
        taskManager.addSubtask(subtask4, epic);

        assertEquals(Status.DONE, epic.getStatus());

        taskManager.clearSubtasks();
        assertEquals(0, epic.getSubtasks().size());

        Subtask subtask5 = new Subtask("subtask5", null, Status.NEW,
                LocalDateTime.of(2005, 11, 21, 21, 59), Duration.ofMinutes(60));
        Subtask subtask6 = new Subtask("subtask6", null, Status.DONE,
                LocalDateTime.of(2006, 11, 21, 21, 59), Duration.ofMinutes(60));

        taskManager.addSubtask(subtask5, epic);
        taskManager.addSubtask(subtask6, epic);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        taskManager.clearSubtasks();

        Subtask subtask7 = new Subtask("subtask7", null, Status.IN_PROGRESS,
                LocalDateTime.of(2005, 11, 21, 21, 59), Duration.ofMinutes(60));
        Subtask subtask8 = new Subtask("subtask8", null, Status.IN_PROGRESS,
                LocalDateTime.of(2006, 11, 21, 21, 59), Duration.ofMinutes(60));

        taskManager.addSubtask(subtask7, epic);
        taskManager.addSubtask(subtask8, epic);

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
                Status.NEW), taskManager.getEpic(epicId));
        int subtaskId2 = taskManager.addSubtask(new Subtask("subtask2", "asdfas", Status.NEW),
                taskManager.getEpic(epicId));
        int subtaskId3 = taskManager.addSubtask(new Subtask("subtask3", "asdfas", Status.NEW),
                taskManager.getEpic(epicId));

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
        Subtask subtask = new Subtask("asda", "asdfas", Status.NEW);
        taskManager.addSubtask(subtask, epic);
        taskManager.getSubtask(subtask.getId());

        assertEquals(taskManager.getHistory().get(0), subtask);
    }

    @Test
    public void deletedTaskShouldBeDeletedInHistoryManager() {
        int taskId = taskManager.addTask(new Task("asdfa", "asdasdf", Status.NEW));
        int epicId = taskManager.addEpic(new Epic("epic", "asdasdf"));
        int subtaskId = taskManager.addSubtask(new Subtask("sub", "asdasd", Status.DONE),
                taskManager.getEpic(epicId));

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
            taskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW), epic);
            i++;
            taskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW), epic);
            i++;
            taskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW), epic);
            i++;
        }

        taskManager.clearEpics();
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    public void clearEpicsMethodShouldClearEpicsAndSubtasks() {
        for (int i = 0; i < 10; i++) {
            Epic epic = taskManager.getEpic(taskManager.addEpic(new Epic(String.valueOf(i), null)));
            taskManager.addSubtask(new Subtask(String.valueOf(i), null, Status.NEW), epic);
            taskManager.addSubtask(new Subtask(String.valueOf(i + 35), null, Status.NEW), epic);
            taskManager.addSubtask(new Subtask(String.valueOf(i + 78), null, Status.NEW), epic);
        }

        taskManager.clearEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    public void getHistoryTest() {
        List<Task> history = taskManager.getHistory();
        assertEquals(0, history.size());

        int taskId = taskManager.addTask(new Task("asdfa", "asdasdf", Status.NEW));
        int epicId = taskManager.addEpic(new Epic("epic", "asdasdf"));
        int subtaskId = taskManager.addSubtask(new Subtask("sub", "asdasd", Status.DONE),
                taskManager.getEpic(epicId));

        List<Task> expectedList = new ArrayList<>();
        expectedList.add(taskManager.getEpic(epicId));
        expectedList.add(taskManager.getTask(taskId));
        expectedList.add(taskManager.getSubtask(subtaskId));

        history = taskManager.getHistory();

        assertEquals(expectedList.reversed(), history);
    }

    @Test
    public void clearHistoryTest() {
        List<Task> history = taskManager.getHistory();
        taskManager.clearHistory();
        assertEquals(0, history.size());

        Epic epic = new Epic("epic", "asdasdf");
        int taskId = taskManager.addTask(new Task("asdfa", "asdasdf", Status.NEW));
        int epicId = taskManager.addEpic(epic);
        int subtaskId = taskManager.addSubtask(new Subtask("sub", "asdasd", Status.DONE),
                epic);

        taskManager.getTask(taskId);
        taskManager.getEpic(epicId);
        taskManager.getSubtask(subtaskId);

        assertEquals(3, taskManager.getHistory().size());

        taskManager.clearHistory();

        assertEquals(0, taskManager.getHistory().size());

    }

    @Test
    public void getTasksTest() {
        assertEquals(0, taskManager.getTasks().size());

        taskManager.addTask(new Task("abc1", null, Status.DONE,
                LocalDateTime.of(2020, 1, 21, 20, 59), Duration.ofMinutes(110)));
        taskManager.addTask(new Task("abc2", null, Status.DONE,
                LocalDateTime.of(2020, 2, 21, 20, 59), Duration.ofMinutes(110)));
        taskManager.addTask(new Task("abc3", null, Status.DONE,
                LocalDateTime.of(2020, 3, 21, 20, 59), Duration.ofMinutes(110)));

        assertEquals(3, taskManager.getTasks().size());
    }

    @Test
    public void clearTasksTest() {
        taskManager.addTask(new Task("abc1", null, Status.DONE,
                LocalDateTime.of(2020, 1, 21, 20, 59), Duration.ofMinutes(110)));
        taskManager.addTask(new Task("abc2", null, Status.DONE,
                LocalDateTime.of(2020, 2, 21, 20, 59), Duration.ofMinutes(110)));
        taskManager.addTask(new Task("abc3", null, Status.DONE,
                LocalDateTime.of(2020, 3, 21, 20, 59), Duration.ofMinutes(110)));

        assertEquals(3, taskManager.getTasks().size());

        taskManager.clearTasks();

        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void addTaskTest() {
        taskManager.addTask(new Task("abc1", null, Status.DONE,
                LocalDateTime.of(2020, 1, 21, 20, 59), Duration.ofMinutes(110)));
        taskManager.addTask(new Task("abc2", null, Status.DONE,
                LocalDateTime.of(2020, 2, 21, 20, 59), Duration.ofMinutes(110)));
        taskManager.addTask(new Task("abc3", null, Status.DONE,
                LocalDateTime.of(2020, 3, 21, 20, 59), Duration.ofMinutes(110)));

        assertEquals(3, taskManager.getTasks().size());

        taskManager.addTask(new Task("abc4", null, Status.DONE,
                LocalDateTime.of(2020, 3, 21, 20, 59), Duration.ofMinutes(110)));

        assertEquals(3, taskManager.getTasks().size()); //Найдено пересечение, задача не должна добавиться

        taskManager.addTask(null);

        assertEquals(3, taskManager.getTasks().size());
    }

    @Test
    public void removeTaskTest() {
        taskManager.addTask(new Task("abc1", null, Status.DONE,
                LocalDateTime.of(2020, 1, 21, 20, 59), Duration.ofMinutes(110)));
        taskManager.addTask(new Task("abc2", null, Status.DONE,
                LocalDateTime.of(2020, 2, 21, 20, 59), Duration.ofMinutes(110)));
        int taskId = taskManager.addTask(new Task("abc3", null, Status.DONE,
                LocalDateTime.of(2020, 3, 21, 20, 59), Duration.ofMinutes(110)));

        assertEquals(3, taskManager.getTasks().size());

        taskManager.removeTask(taskId);

        assertEquals(2, taskManager.getTasks().size());

        taskManager.removeTask(taskId); //такого id больше нет в taskManager

        assertEquals(2, taskManager.getTasks().size());
    }

    @Test
    public void getTaskTest() {
        taskManager.addTask(new Task("abc1", null, Status.DONE,
                LocalDateTime.of(2020, 1, 21, 20, 59), Duration.ofMinutes(110)));
        taskManager.addTask(new Task("abc2", null, Status.DONE,
                LocalDateTime.of(2020, 2, 21, 20, 59), Duration.ofMinutes(110)));

        Task task = new Task("abc3", null, Status.DONE,
                LocalDateTime.of(2020, 3, 21, 20, 59), Duration.ofMinutes(110));
        int taskId = taskManager.addTask(task);

        assertEquals(task, taskManager.getTask(taskId));

        assertEquals(null, taskManager.getTask(260));
    }

    @Test
    public void updateTaskTest() {
        int oldTaskId = taskManager.addTask(new Task("abc1", null, Status.DONE,
                LocalDateTime.of(2020, 1, 21, 20, 59), Duration.ofMinutes(110)));
        Task newTask = new Task("abc2", null, Status.DONE,
                LocalDateTime.of(2020, 4, 21, 20, 59), Duration.ofMinutes(110));

        taskManager.updateTask(oldTaskId, newTask);

        assertEquals(newTask, taskManager.getTask(oldTaskId));

        taskManager.updateTask(oldTaskId, null);

        assertEquals(newTask, taskManager.getTask(oldTaskId));

        assertEquals(1, taskManager.getTasks().size());
        taskManager.updateTask(12312, null);

        assertEquals(1, taskManager.getTasks().size());

    }

    @Test
    public void getEpicsTest() {
        assertEquals(0, taskManager.getEpics().size());

        taskManager.addEpic(new Epic("epic1", "asdasdf"));
        taskManager.addEpic(new Epic("epic2", "asdasdf"));

        assertEquals(2, taskManager.getEpics().size());
    }

    @Test
    public void clearEpicsTest() {
        assertEquals(0, taskManager.getEpics().size());

        taskManager.addEpic(new Epic("epic1", "asdasdf"));
        taskManager.addEpic(new Epic("epic2", "asdasdf"));

        assertEquals(2, taskManager.getEpics().size());

        taskManager.clearEpics();

        assertEquals(0, taskManager.getEpics().size());

    }

    @Test
    public void addEpicTest() {
        taskManager.addEpic(new Epic("epic1", "asdasdf"));

        assertEquals(1, taskManager.getEpics().size());

        taskManager.addEpic(null);

        assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    public void removeEpicTest() {
        Epic epic = new Epic("epic1", "asdasdf");

        taskManager.addEpic(epic);

        assertEquals(1, taskManager.getEpics().size());

        taskManager.removeEpic(epic.getId());

        assertEquals(0, taskManager.getEpics().size());

        taskManager.removeEpic(123);

        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    public void getEpicTest() {
        Epic epic = new Epic("epic1", "asdasdf");

        int epicId = taskManager.addEpic(epic);

        assertEquals(epic, taskManager.getEpic(epicId));

        assertNull(taskManager.getEpic(1231245));
    }

    @Test
    public void updateEpicTest() {
        Epic oldEpic = new Epic("epic1", "asdasdf");

        int oldEpicId = taskManager.addEpic(oldEpic);

        assertEquals(oldEpic, taskManager.getEpic(oldEpicId));

        Epic newEpic = new Epic("epic2", "asdasdf");

        taskManager.updateEpic(oldEpicId, newEpic);

        newEpic.setId(1);

        assertEquals(newEpic, taskManager.getEpic(oldEpicId));

        taskManager.updateEpic(oldEpicId, null);

        assertEquals(newEpic, taskManager.getEpic(oldEpicId));

        taskManager.updateEpic(1321, newEpic);

        assertEquals(newEpic, taskManager.getEpic(oldEpicId));
    }

    @Test
    public void getSubtasksOfEpicTest() {
        Epic epic = new Epic("epic1", "asdasdf");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "desc", Status.DONE);
        Subtask subtask2 = new Subtask("subtask2", "desc", Status.DONE);
        Subtask subtask3 = new Subtask("subtask3", "desc", Status.DONE);
        taskManager.addSubtask(subtask1, epic);
        taskManager.addSubtask(subtask2, epic);
        taskManager.addSubtask(subtask3, epic);

        List<Subtask> expectedList = new ArrayList<>();
        expectedList.add(subtask1);
        expectedList.add(subtask2);
        expectedList.add(subtask3);


        assertEquals(expectedList, taskManager.getSubtasksOfEpic(epic.getId()));
    }

    //Subtasks
    @Test
    public void getSubtasksTest() {
        assertEquals(0, taskManager.getSubtasks().size());

        Epic epic = new Epic("epic1", "asdasdf");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "desc", Status.DONE);
        Subtask subtask2 = new Subtask("subtask2", "desc", Status.DONE);
        Subtask subtask3 = new Subtask("subtask3", "desc", Status.DONE);

        taskManager.addSubtask(subtask1, epic);
        taskManager.addSubtask(subtask2, epic);
        taskManager.addSubtask(subtask3, epic);

        assertEquals(3, taskManager.getSubtasks().size());
    }

    @Test
    public void addSubtaskTest() {
        assertEquals(0, taskManager.getSubtasks().size());

        Epic epic = new Epic("epic1", "asdasdf");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "desc", Status.DONE);
        Subtask subtask2 = new Subtask("subtask2", "desc", Status.DONE);

        taskManager.addSubtask(subtask1, epic);
        taskManager.addSubtask(subtask2, epic);

        assertEquals(2, taskManager.getSubtasks().size());

        Subtask subtask3 = new Subtask("subtask3", "desc", Status.DONE);

        taskManager.addSubtask(subtask3, epic);

        assertEquals(3, taskManager.getSubtasks().size());
        assertEquals(3, taskManager.getSubtasksOfEpic(epicId).size());
    }

    @Test
    public void removeSubtaskTest() {
        Epic epic = new Epic("epic1", "asdasdf");
        taskManager.addEpic(epic);

        int subtask1Id = taskManager.addSubtask(new Subtask("subtask1", "desc", Status.DONE), epic);
        int subtask2Id = taskManager.addSubtask(new Subtask("subtask2", "desc", Status.DONE), epic);

        assertEquals(2, taskManager.getSubtasks().size());

        taskManager.removeSubtask(subtask1Id);

        assertEquals(1, taskManager.getSubtasks().size());

        taskManager.removeSubtask(123124);

        assertEquals(1, taskManager.getSubtasks().size());
    }

    @Test
    public void getSubtaskTest() {
        Epic epic = new Epic("epic1", "asdasdf");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "desc", Status.DONE);
        Subtask subtask2 = new Subtask("subtask2", "desc", Status.DONE);

        taskManager.addSubtask(subtask1, epic);
        taskManager.addSubtask(subtask2, epic);

        assertEquals(subtask1, taskManager.getSubtask(subtask1.getId()));
        assertNull(taskManager.getSubtask(5462));
    }

    @Test
    public void updateSubtaskTest() {
        Epic epic = new Epic("epic1", "asdasdf");

        int epicId = taskManager.addEpic(epic);

        int subtask1Id = taskManager.addSubtask(new Subtask("subtask1", "desc", Status.DONE), epic);

        assertEquals(1, taskManager.getSubtasks().size());

        Subtask subtask2 = new Subtask("subtask2", "desc", Status.DONE);

        taskManager.updateSubtask(subtask1Id, subtask2);

        assertEquals(subtask2, taskManager.getSubtask(subtask1Id));

        taskManager.updateSubtask(subtask1Id, null);

        assertEquals(subtask2, taskManager.getSubtask(subtask1Id));

        taskManager.updateSubtask(1235412, null);

        assertEquals(1, taskManager.getSubtasks().size());
    }

    @Test
    public void clearSubtasksTest(){
        Epic epic = new Epic("epic1", "asdasdf");
        int epicId = taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("subtask1", "desc", Status.DONE);
        Subtask subtask2 = new Subtask("subtask2", "desc", Status.DONE);

        taskManager.addSubtask(subtask1, epic);
        taskManager.addSubtask(subtask2, epic);

        assertEquals(2, taskManager.getSubtasks().size());

        taskManager.clearSubtasks();

        assertEquals(0, taskManager.getSubtasks().size());

        taskManager.clearSubtasks();

        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void getPrioritizedTasksTest() {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        Task task1 = new Task("task", null, Status.DONE,
                LocalDateTime.of(2000, 11, 21, 21, 51), Duration.ofMinutes(60));
        Task task2 = new Task("task", null, Status.DONE,
                LocalDateTime.of(2000, 11, 21, 22, 51), Duration.ofMinutes(60));
        Epic epic = new Epic("epic", "desc");
        int epicId = taskManager.addEpic(epic);


        Subtask subtask1 = new Subtask("subtask1", "desc", Status.DONE,
                LocalDateTime.of(2001, 11, 1, 10, 53), Duration.ofMinutes(5));
        Subtask subtask2 = new Subtask("subtask2", "desc", Status.DONE,
                LocalDateTime.of(2001, 12, 1, 10, 53), Duration.ofMinutes(5));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addSubtask(subtask1, epic);
        taskManager.addSubtask(subtask2, epic);


        List<Task> expectedList = new ArrayList<>();
        expectedList.add(task1);
        expectedList.add(task2);
        expectedList.add(subtask1);
        expectedList.add(subtask2);

        assertEquals(expectedList, taskManager.getPrioritizedTasks());
    }

    @Test
    public void findIntersectionTest() {
        Task task1 = new Task("task1", null, Status.DONE,
                LocalDateTime.of(2000, 11, 21, 21, 51), Duration.ofMinutes(60));
        Task task2 = new Task("task2", null, Status.DONE,
                LocalDateTime.of(2000, 11, 21, 22, 51), Duration.ofMinutes(60));
        Task task3 = new Task("task3", null, Status.DONE,
                LocalDateTime.of(2000, 11, 21, 20, 51), Duration.ofMinutes(60));
        Epic epic = new Epic("epic", "desc");
        Subtask subtask1 = new Subtask("subtask1", "desc", Status.DONE,
                LocalDateTime.of(2000, 11, 21, 21, 51), Duration.ofMinutes(5));
        Subtask subtask2 = new Subtask("subtask2", "desc", Status.DONE,
                LocalDateTime.of(2000, 11, 21, 22, 51), Duration.ofMinutes(5));
        epic.getSubtasks().add(subtask1);
        epic.getSubtasks().add(subtask2);

        taskManager.addTask(task1);

        assertFalse(taskManager.findIntersection(task2));
        assertFalse(taskManager.findIntersection(task3));



        Task task4 = new Task("task4", null, Status.DONE,
                LocalDateTime.of(2000, 11, 21, 21, 30), Duration.ofMinutes(30));

        assertTrue(taskManager.findIntersection(task4));

        Task task5 = new Task("task5", null, Status.DONE,
                LocalDateTime.of(2000, 11, 21, 22, 20), Duration.ofMinutes(60));

        assertTrue(taskManager.findIntersection(task5));
    }
}
