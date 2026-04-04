package blocksmith.domain.block;

import blocksmith.domain.connection.PortRef;
import java.util.Map;

/**
 *
 * @author joostmeulenkamp
 */
public interface OutputExtractor {

    Map<PortRef, Object> extract(BlockId block, Object result);
}
