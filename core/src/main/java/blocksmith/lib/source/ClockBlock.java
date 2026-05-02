package blocksmith.lib.source;

import java.util.Optional;
import java.util.function.Consumer;
import blocksmith.exec.block.SourceBlock;
import blocksmith.infra.blockloader.annotations.Block;
import blocksmith.utils.icons.FontAwesomeSolid;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author joost
 */
@Block(
        type = "Time.clock",
        name = "Clock",
        description = "Emits the current time every second",
        icon = FontAwesomeSolid.CLOCK,
        category = "Time")
public class ClockBlock implements SourceBlock {

    private ScheduledExecutorService scheduler;
    private volatile Exception error;

    @Override
    public void start(Consumer<Object> emitter) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                var value = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                System.out.println(value + " EMITTED");
                emitter.accept(value);
            } catch (Exception e) {
                error = e;
                stop();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    @Override
    public boolean isRunning() {
        return scheduler != null && !scheduler.isShutdown();
    }

    public String outputs() {
        return null;
    }

    @Override
    public Optional<Exception> error() {
        return Optional.ofNullable(error);
    }
}
