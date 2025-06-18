import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class InMemoryHistoryManagerTest {
    @Test
    public void getHistoryShouldReturnListOfTasks() {
        InMemoryHistoryManager inMemoryHistoryManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
        ArrayList<Task> expectedArrayList = new ArrayList<>();
        expectedArrayList.add(new Epic("adsf", null, new ArrayList<>()));
        expectedArrayList.add(new Task("adsf", null, Status.NEW));
        expectedArrayList.add(new Subtask("sadfas", null,Status.NEW, new Epic("asdfxzv", null, new ArrayList<>())));

        inMemoryHistoryManager.add(expectedArrayList.get(0));
        inMemoryHistoryManager.add(expectedArrayList.get(1));
        inMemoryHistoryManager.add(expectedArrayList.get(2));

        assertEquals(expectedArrayList, inMemoryHistoryManager.getHistory());
    }

    @Test
    public void taskInHistoryManagerAndTheSameTaskInTaskManagerShouldBeEqual() {
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
        InMemoryHistoryManager inMemoryHistoryManager = inMemoryTaskManager.getHistoryManager();

        Task task = new Task("Помыть машину", null, Status.NEW);
        inMemoryTaskManager.addTask(task);
        Task taskFromTaskManager = inMemoryTaskManager.getTask(task.getId());

        Task taskFromHistoryManager = inMemoryHistoryManager.getHistory().get(0);

        assertEquals(taskFromTaskManager, taskFromHistoryManager);
    }

    @Test
    public void epicInHistoryManagerAndTheSameEpicInTaskManagerShouldBeEqual() {
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
        InMemoryHistoryManager inMemoryHistoryManager = inMemoryTaskManager.getHistoryManager();

        Epic epic = new Epic("Поехать в другой город", null, new ArrayList<>());
        inMemoryTaskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Собрать вещи", null, Status.NEW, epic);
        Subtask subtask2 = new Subtask("Прибраться", null, Status.NEW, epic);
        Subtask subtask3 = new Subtask("Умыться", null, Status.NEW, epic);

        Epic epicFromTaskManager = inMemoryTaskManager.getEpic(epic.getId());
        Epic epicFromHistoryManager = (Epic) inMemoryTaskManager.getHistoryManager().getHistory().get(0);
        assertEquals(epicFromHistoryManager, epicFromTaskManager);
    }

    @Test
    public void historyManagerShouldContain10Tasks() {
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
        InMemoryHistoryManager inMemoryHistoryManager = inMemoryTaskManager.getHistoryManager();
        int expectedHistorySize = 10;

        for (int i = 0; i < 12; i++) {
            inMemoryTaskManager.addTask(new Task(String.valueOf(i), null, Status.NEW));
        }

        for (Task task : inMemoryTaskManager.getTasks().values()) {
            inMemoryTaskManager.getTask(task.getId());
        }

        assertEquals(10, inMemoryHistoryManager.getHistory().size());
    }
}
