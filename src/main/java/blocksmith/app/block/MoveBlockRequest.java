package blocksmith.app.block;

import blocksmith.domain.block.BlockId;

/**
 *
 * @author joost
 */
public record MoveBlockRequest(
        BlockId id,
        double x,
        double y) {
}
