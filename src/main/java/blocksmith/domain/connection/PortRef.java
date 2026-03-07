package blocksmith.domain.connection;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.value.Port.Direction;
import java.util.Objects;

/**
 *
 * @author joostmeulenkamp
 */
public record PortRef(
        BlockId blockId,
        Direction direction,
        String valueId) {

    public PortRef   {
        Objects.requireNonNull(blockId);
        Objects.requireNonNull(direction);
        Objects.requireNonNull(valueId);
    }

    public PortRef withBlockId(BlockId blockId) {
        return new PortRef(blockId, direction, valueId);
    }

    public static PortRef of(BlockId blockId, Direction direction, String valueId) {
        return new PortRef(blockId, direction, valueId);
    }

    public static PortRef input(BlockId blockId, String valueId) {
        return new PortRef(blockId, Direction.INPUT, valueId);
    }

    public static PortRef output(BlockId blockId, String valueId) {
        return new PortRef(blockId, Direction.OUTPUT, valueId);
    }
}
