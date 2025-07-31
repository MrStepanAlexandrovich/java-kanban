package manager;

import org.junit.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InMemoryHistoryManagerTest {
    @Test
    public void getHistoryShouldReturnListOfTasks() {
        TaskManager taskManager = Managers.getDefault();

        int idEpic = taskManager.addEpic(new Epic("adsf", null, new ArrayList<>()));
        int idTask = taskManager.addTask(new Task("adsfzcx", null, Status.NEW));
        int idSubtask = taskManager.addSubtask(new Subtask("sadfas", null, Status.NEW, taskManager.getEpic(idEpic)));

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
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("Помыть машину", null, Status.NEW);
        taskManager.addTask(task);
        Task taskFromTaskManager = taskManager.getTask(task.getId());

        Task taskFromHistoryManager = taskManager.getHistory().get(0);

        assertEquals(taskFromTaskManager, taskFromHistoryManager);
    }

    @Test
    public void epicInHistoryManagerAndTheSameEpicInTaskManagerShouldBeEqual() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic = new Epic("Поехать в другой город", null, new ArrayList<>());
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
        TaskManager taskManager = Managers.getDefault();

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
}
