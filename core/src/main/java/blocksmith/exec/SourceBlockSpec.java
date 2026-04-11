package blocksmith.exec;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 *
 * @author joost
 */
public record SourceBlockSpec(
        Supplier<SourceBlock> factory,
        BiConsumer<SourceBlock, Object[]> injector
        ) implements BlockExecutable {

}
