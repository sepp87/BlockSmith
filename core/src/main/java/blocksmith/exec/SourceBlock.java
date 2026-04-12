package blocksmith.exec;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author joostmeulenkamp
 */
public interface SourceBlock {
    
    public static final String INPUT_METHOD = "inputs";
    public static final String OUTPUT_METHOD = "outputs";

    void start(Consumer<Object> outputListener);

    void stop();

    boolean isRunning();

    Optional<Exception> error();

}
