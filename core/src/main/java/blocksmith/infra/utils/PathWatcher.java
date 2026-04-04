package blocksmith.infra.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;

/**
 *
 * @author joost
 */
public class PathWatcher {

    public static Thread watchFile(Path path, Consumer<Path> pathListener) {
        var thread = new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                System.out.println("Watching " + path);

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
                e.printStackTrace();
            }
        });
        thread.start();
        return thread;
    }
}
