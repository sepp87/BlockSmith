package blocksmith.exec.block;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joost
 */
public record BlockState(
        BlockId id,
        BlockStatus status,
        Map<PortRef, Object> values,
        List<BlockException> exceptions) {

}
