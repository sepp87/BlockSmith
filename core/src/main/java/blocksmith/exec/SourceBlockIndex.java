package blocksmith.exec;

import blocksmith.app.block.BlockLibrary;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.graph.GraphDiff;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author joost
 */
public class SourceBlockIndex {

    private final BlockLibrary library;

    private final Map<BlockId, SourceBlock> index = new HashMap<>();

    public SourceBlockIndex(BlockLibrary library) {
        this.library = library;
    }
    
    public void updateFrom(GraphDiff diff) {
        var added = diff.addedBlocks();
        var removed = diff.removedBlocks();
        
        registerSourceBlocksWithin(added);
        unregisterSourceBlocksWithin(removed);
    }

    private void registerSourceBlocksWithin(Collection<Block> added) {
        for (var candidate : added) {
            var type = candidate.type();
            var exec = library.execs().resolve(type).orElse(null);
            if (exec instanceof SourceBlockSpec spec) {
                var id = candidate.id();
                var source = spec.factory().get();
                index.put(id, source);
            }
        }
    }


    private void unregisterSourceBlocksWithin(Collection<Block> removed) {
        for (var candidate : removed) {
            var id = candidate.id();
            var source = index.remove(id);
            source.stop();
        }
    }

    public Optional<SourceBlock> get(BlockId id) {
        return Optional.ofNullable(index.get(id));
    }
     
}
