package blocksmith.domain.group;

import blocksmith.domain.block.BlockId;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joost
 */
public record Group(
        String label,
        List<BlockId> blocks) {

    public boolean isEmpty() {
        return blocks.isEmpty();
    }
    
    public boolean contains(BlockId id) {
        return blocks.contains(id);
    }

    public Group withBlock(BlockId id) {
        var updated = new ArrayList<BlockId>(blocks);
        updated.add(id);
        return new Group(label, updated);
    }

    public Group withoutBlock(BlockId id) {
        var updated = new ArrayList<BlockId>(blocks);
        updated.remove(id);
        return new Group(label, updated);
    }
}
