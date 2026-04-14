package blocksmith.app.outbound;

/**
 *
 * @author joost
 */
public interface AppScheduler {

    void runInBackground(Runnable task);

    void runOnMainThread(Runnable task);
}
