package blocksmith.exec;

import blocksmith.domain.block.BlockDef;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.exec.BlockException.Severity;
import java.util.HashMap;
import java.util.Map;
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

        IntermediateResult result = null;

        try {
            result = new UnifiedMethodExecutor(def, func).execute(args);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        var values = def.outputExtractor().extract(block, result.getData());
        return new ExecutionResult(values, result.exceptions());
    }

}
