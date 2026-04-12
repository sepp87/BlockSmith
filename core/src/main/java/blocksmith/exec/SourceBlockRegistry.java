package blocksmith.exec;

import blocksmith.app.block.BlockExecLibrary;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author joost
 */
public class SourceBlockRegistry {

    private final BlockExecLibrary library;

    private final Map<BlockId, SourceBlock> blocks = new HashMap<>();

    public SourceBlockRegistry(BlockExecLibrary library) {
        this.library = library;
    }

    public void registerSourceBlocksWithin(Collection<Block> added) {
        for (var candidate : added) {
            var type = candidate.type();
            var exec = library.resolve(type).orElse(null);
            if (exec instanceof SourceBlockSpec spec) {
                var id = candidate.id();
                var source = spec.factory().get();
                blocks.put(id, source);
            }
        }
    }


    public void unregisterSourceBlocksWithin(Collection<Block> removed) {
        for (var candidate : removed) {
            var id = candidate.id();
            var source = blocks.remove(id);
            source.stop();
        }
    }

}
