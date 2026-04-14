package blocksmith.ui;

import blocksmith.app.outbound.AppScheduler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;

/**
 *
 * @author joost
 */
public class UiAppScheduler implements AppScheduler {
    
    private final ExecutorService background;
    
    public UiAppScheduler() {
        this.background = Executors.newVirtualThreadPerTaskExecutor();
    }
    
    @Override
    public void runInBackground(Runnable task) {
        background.execute(task);
    }
    
    @Override
    public void runOnMainThread(Runnable task) {
        Platform.runLater(task);
    }
    
}
