package blocksmith.domain.group;

import blocksmith.domain.block.BlockId;
import java.util.List;

/**
 *
 * @author joost
 */
public record Group(
        String label,
        List<BlockId> blocks) {

}
