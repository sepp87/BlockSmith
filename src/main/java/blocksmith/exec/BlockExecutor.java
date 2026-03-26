package blocksmith.exec;

import blocksmith.domain.block.BlockDef;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.exec.BlockException.Severity;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockExecutor {

    private static final Logger LOGGER = Logger.getLogger(BlockExecutor.class.getName());

    private final BlockId block;
    private final BlockDef def;
    private final BlockFunc func;

    public BlockExecutor(BlockId block, BlockDef def, BlockFunc func) {
        this.block = block;
        this.def = def;
        this.func = func;
    }

    public ExecutionResult invoke(Object... args) {

        final IntermediateResult[] result = {null}; // Use an array instead of AtomicReference

        try {
            result[0] = new UnifiedMethodExecutor(def, func).execute(args);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        var values = new HashMap<PortRef, Object>();
        values.put(PortRef.output(block, "value"), result[0].getData());
        return new ExecutionResult(values, result[0].exceptions());

    }

}
