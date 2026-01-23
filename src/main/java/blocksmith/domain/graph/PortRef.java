package blocksmith.domain.graph;

import blocksmith.domain.block.Port.Direction;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author joostmeulenkamp
 */
public record PortRef(
        UUID blockId,
        Direction direction,
        int index) {

    public PortRef   {
        Objects.requireNonNull(blockId);
        Objects.requireNonNull(direction);
        if (index < 0) {
            throw new IllegalArgumentException("Port index must be >= 0");
        }
    }

}
