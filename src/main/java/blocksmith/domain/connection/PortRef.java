package blocksmith.domain.connection;

import blocksmith.domain.block.BlockId;
import java.util.Objects;

/**
 *
 * @author joostmeulenkamp
 */
public record PortRef(
        BlockId blockId,
        String valueId) {

    public PortRef   {
        Objects.requireNonNull(blockId);
        Objects.requireNonNull(valueId);
    }

}
