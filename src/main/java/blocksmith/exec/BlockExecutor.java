package blocksmith.exec;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockDef;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.exec.BlockException.Severity;
import blocksmith.ui.graph.block.MethodBlockNew;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockExecutor {

    private final BlockId block;
    private final BlockDef def;
    private final BlockFunc func;
    private final boolean isListOperator;

    public BlockExecutor(BlockId block, BlockDef def, BlockFunc func, boolean isListOperator) {
        this.block = block;
        this.def = def;
        this.func = func;
        this.isListOperator = isListOperator;
    }

    public ExecutionResult invoke(Object... args) {

        final IntermediateResult[] result = {null}; // Use an array instead of AtomicReference

        try {

            if (true) {
                result[0] = new UnifiedMethodExecutor(def, func).execute(args);

            } else {

                if (!isListOperator) {
                    result[0] = new MethodExecutor(def, func).invoke(args);

                } else {
                    var listMethodExecutor = new ListMethodExecutor(def, func);

                    if (args.length == 1) {
                        result[0] = listMethodExecutor.invoke(args);

                    } else if (args.length == 2) {
                        result[0] = listMethodExecutor.invoke2(args[0], args[1]);

                    } else {
                        // Show an error when there are more than 3 ports
                        IntermediateResult fallback = new IntermediateResult();
                        BlockException exception = new BlockException(null, Severity.ERROR, new IndexOutOfBoundsException("No more than 2 input ports are supported list operators."));
                        fallback.exceptions().add(exception);
                        result[0] = fallback;

                    }
                }
            }

        } catch (Exception e) {
            Logger.getLogger(MethodBlockNew.class.getName()).log(Level.SEVERE, null, e);
        }
        
        var values = new HashMap<PortRef, Object>();
        values.put(PortRef.output(block, "value"), result[0].getData());
        return new ExecutionResult(values, result[0].exceptions());

    }

}
