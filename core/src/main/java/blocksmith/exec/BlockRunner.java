package blocksmith.exec;

import blocksmith.domain.block.BlockDef;
import blocksmith.domain.block.BlockId;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockRunner {

    private static final Logger LOGGER = Logger.getLogger(BlockRunner.class.getName());

    private final BlockId block;
    private final BlockDef def;
    private final BlockFunc func;

    public BlockRunner(BlockId block, BlockDef def, BlockFunc func) {
        this.block = block;
        this.def = def;
        this.func = func;
    }

    public BlockFuncResult invoke(Object... args) {

        IntermediateResult result = null;

        try {
            result = new UnifiedMethodExecutor(def, func).execute(args);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        var values = def.outputExtractor().extract(block, result.getData());
        return new BlockFuncResult(values, result.exceptions());
    }

}
