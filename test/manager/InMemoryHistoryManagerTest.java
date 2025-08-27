package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void addAndRemoveTest() {
        Task task = new Task("task", null, Status.DONE);
        task.setId(1);
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());

        historyManager.remove(1);

        assertEquals(0, historyManager.getHistory().size());

    }

    @Test
    public void getHistoryShouldReturnListOfTasks() {
        int idEpic = taskManager.addEpic(new Epic("adsf", null));
        int idTask = taskManager.addTask(new Task("adsfzcx", null, Status.NEW));
        int idSubtask = taskManager.addSubtask(new Subtask("sadfas", null, Status.NEW, null),
                taskManager.getEpic(idEpic));

        Epic epic = taskManager.getEpic(idEpic);
        Task task = taskManager.getTask(idTask);
        Subtask subtask = taskManager.getSubtask(idSubtask);

        assertEquals(subtask, taskManager.getHistory().get(0));
        assertEquals(task, taskManager.getHistory().get(1));
        assertEquals(epic, taskManager.getHistory().get(2));
        assertEquals(taskManager.getHistory().size(), 3);
    }

    @Test
    public void taskInHistoryManagerAndTheSameTaskInTaskManagerShouldBeEqual() {
        Task task = new Task("Помыть машину", null, Status.NEW);
        taskManager.addTask(task);
        Task taskFromTaskManager = taskManager.getTask(task.getId());

        Task taskFromHistoryManager = taskManager.getHistory().get(0);

        assertEquals(taskFromTaskManager, taskFromHistoryManager);
    }

    @Test
    public void epicInHistoryManagerAndTheSameEpicInTaskManagerShouldBeEqual() {
        Epic epic = new Epic("Поехать в другой город", null);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Собрать вещи", null, Status.NEW, epic);
        Subtask subtask2 = new Subtask("Прибраться", null, Status.NEW, epic);
        Subtask subtask3 = new Subtask("Умыться", null, Status.NEW, epic);

        Epic epicFromTaskManager = taskManager.getEpic(epic.getId());
        List<Task> history = taskManager.getHistory();
        Epic epicFromHistoryManager = (Epic) taskManager.getHistory().get(0);
        assertEquals(epicFromHistoryManager, epicFromTaskManager);
    }

    @Test
    public void historyManagerShouldFillWhenGetTask() {
        int expectedHistorySize = 12;

        for (int i = 0; i < 12; i++) {
            taskManager.addTask(new Task(String.valueOf(i), null, Status.NEW));
        }

        for (Task task : taskManager.getTasks()) {
            taskManager.getTask(task.getId());
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(expectedHistorySize, history.size());
    }

    @Test
    public void tasksShouldNotDuplicate() {
        Task task1 = taskManager.getTask(taskManager.addTask(new Task("task1", null, Status.DONE)));
        Task task2 = taskManager.getTask(taskManager.addTask(new Task("task2", null, Status.DONE)));
        Task task3 = taskManager.getTask(taskManager.addTask(new Task("task3", null, Status.DONE)));

        assertEquals(3, taskManager.getHistory().size());

        taskManager.getTask(task3.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task3.getId());

        assertEquals(3, taskManager.getHistory().size());

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());

        assertEquals(3, taskManager.getHistory().size());
    }

    @Test
    public void emptyHistory() {
        taskManager.addTask(new Task("name1", null, Status.DONE));
        taskManager.addTask(new Task("name2", null, Status.DONE));
        taskManager.addTask(new Task("name3", null, Status.DONE));

        assertEquals(0, taskManager.getHistory().size());
    }
}
