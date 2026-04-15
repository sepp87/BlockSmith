package blocksmith.exec;

import blocksmith.domain.connection.PortRef;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joost
 */
public record BlockFuncResult(
        Map<PortRef, Object> values,
        List<BlockException> exceptions) implements ExecutionOutcome {

}
