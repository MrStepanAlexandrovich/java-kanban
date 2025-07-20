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
        HistoryManager historyManager = Managers.getDefaultHistory();
        ArrayList<Task> expectedArrayList = new ArrayList<>();

        expectedArrayList.add(new Epic("adsf", null, new ArrayList<>()));
        expectedArrayList.add(new Task("adsfzcx", null, Status.NEW));
        expectedArrayList.add(new Subtask("sadfas", null,Status.NEW, new Epic("asdfxzv", null, new ArrayList<>())));

        historyManager.add(expectedArrayList.get(2));
        historyManager.add(expectedArrayList.get(1));
        historyManager.add(expectedArrayList.get(0));

        assertEquals(expectedArrayList, historyManager.getHistory());
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
        Epic epicFromHistoryManager = (Epic)taskManager.getHistory().get(0);
        assertEquals(epicFromHistoryManager, epicFromTaskManager);
    }

    @Test
    public void historyManagerShouldFillWhenGetTask() {
        TaskManager taskManager = Managers.getDefault();

        int expectedHistorySize = 12;

        for (int i = 0; i < 12; i++) {
            taskManager.addTask(new Task(String.valueOf(i), null, Status.NEW));
        }

        for (Task task : taskManager.getTasks().values()) {
            taskManager.getTask(task.getId());
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(expectedHistorySize, history.size());
    }
}
