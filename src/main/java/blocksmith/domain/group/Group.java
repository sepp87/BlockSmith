package blocksmith.domain.group;

import blocksmith.domain.block.BlockId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author joost
 */
public record Group(
        GroupId id,
        String label,
        List<BlockId> blocks) {

    public final static int MINIMUM_SIZE = 2;

    public Group   {
        Objects.requireNonNull(id);
        Objects.requireNonNull(blocks);
        if (blocks.size() < MINIMUM_SIZE) {
            throw new IllegalArgumentException("Group MUST contain at least two blocks");
        }
    }

    public boolean isEmpty() {
        return blocks.isEmpty();
    }

    public int size() {
        return blocks.size();
    }

    public boolean contains(BlockId block) {
        return blocks.contains(block);
    }

    public Group withBlock(BlockId block) {
        var updated = new ArrayList<BlockId>(blocks);
        updated.add(block);
        return new Group(id, label, updated);
    }

    public Group withoutBlock(BlockId block) {
        if (!contains(block)) {
            return this;
        }
        var updated = new ArrayList<BlockId>(blocks);
        updated.remove(block);
        return new Group(id, label, updated);
    }
}
