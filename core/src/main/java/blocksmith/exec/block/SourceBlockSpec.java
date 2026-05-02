package blocksmith.exec.block;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 *
 * @author joost
 */
public record SourceBlockSpec(
        Supplier<SourceBlock> factory,
        BiConsumer<SourceBlock, List<Object>> injector
        ) implements BlockExec {

}
