package blocksmith.lib.emitter;

import blocksmith.domain.block.BlockDef;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import blocksmith.exec.SourceBlock;

/**
 *
 * @author joost
 */
public class PathWatcherBlock implements SourceBlock {

    @Override
    public void start(Map<String, Consumer<Object>> outputs) {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    public void inputs(Path path) {

    }

    public LocalDate outputs() {
        return null;
    }

    @Override
    public Optional<Exception> error() {
        return Optional.empty();
    }

}
