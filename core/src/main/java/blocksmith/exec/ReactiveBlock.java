package blocksmith.exec;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author joostmeulenkamp
 */
public interface ReactiveBlock {

    void start(Map<String, Consumer<Object>> outputs);

    void stop();

    boolean isRunning();

    Optional<Exception> error();

}
