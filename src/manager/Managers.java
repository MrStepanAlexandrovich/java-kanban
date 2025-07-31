package manager;

import java.io.File;
import java.nio.file.Paths;

public class Managers {
    public static TaskManager getDefault() {
        File file = Paths.get("savedTasks.txt").toFile();
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
