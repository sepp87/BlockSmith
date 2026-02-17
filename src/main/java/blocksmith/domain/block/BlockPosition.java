package blocksmith.domain.block;

import blocksmith.domain.block.BlockId;

/**
 *
 * @author joost
 */
public record BlockPosition(
        BlockId id,
        double x,
        double y) {
}
