package blocksmith.infra.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class PathWatcher {

    private final static Logger LOGGER = Logger.getLogger(PathWatcher.class.getName());

    public static Thread watchFile(Path path, Consumer<Path> pathListener) {
        var thread = new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                LOGGER.log(Level.FINEST, "Watching " + path);

                path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.context().toString().equals(path.getFileName().toString())) {
                            pathListener.accept(path);
                        }
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                LOGGER.log(Level.FINEST, "Stopped watching " + path);

//                e.printStackTrace();
            }
        });
        thread.start();
        return thread;
    }
}
