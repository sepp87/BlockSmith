package blocksmith.exec;

import blocksmith.domain.block.BlockDef;
import blocksmith.exec.BlockException.Severity;
import blocksmith.ui.graph.block.MethodBlockNew;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockExecutor {

    private final BlockDef def;
    private final BlockFunc func;
    private final boolean isListOperator;

    public BlockExecutor(BlockDef def, BlockFunc func, boolean isListOperator) {
        this.def = def;
        this.func = func;
        this.isListOperator = isListOperator;
    }

    public InvocationResult invoke(Object... args) {

        final InvocationResult[] result = {null}; // Use an array instead of AtomicReference

        try {

            if (false) {
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
                        InvocationResult fallback = new InvocationResult();
                        BlockException exception = new BlockException(null, Severity.ERROR, new IndexOutOfBoundsException("No more than 2 input ports are supported list operators."));
                        fallback.exceptions().add(exception);
                        result[0] = fallback;

                    }
                }
            }

        } catch (Exception e) {
            Logger.getLogger(MethodBlockNew.class.getName()).log(Level.SEVERE, null, e);
        }

        return result[0];

    }

}
