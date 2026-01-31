package blocksmith.exec;

import blocksmith.domain.block.BlockDef;
import blocksmith.ui.MethodBlockNew;
import btscore.graph.block.ExceptionPanel;
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

    public MethodExecutor.InvocationResult invoke(Object... parameters) {

        final MethodExecutor.InvocationResult[] result = {null}; // Use an array instead of AtomicReference

        try {
            if (!isListOperator) {
                result[0] = new MethodExecutor(def, func).invoke(parameters);
                
            } else {
                var listMethodExecutor = new ListMethodExecutor(def, func);

                if (parameters.length == 1) {
                    result[0] = listMethodExecutor.invoke(parameters);

                } else if (parameters.length == 2) {
                    result[0] = listMethodExecutor.invoke2(parameters[0], parameters[1]);

                } else {
                    // Show an error when there are more than 3 ports
                    MethodExecutor.InvocationResult fallback = new MethodExecutor.InvocationResult();
                    ExceptionPanel.BlockException exception = new ExceptionPanel.BlockException(null, ExceptionPanel.Severity.ERROR, new IndexOutOfBoundsException("No more than 2 input ports are supported list operators."));
                    fallback.exceptions().add(exception);
                    result[0] = fallback;
                    
                }
            }
        } catch (Exception e) {
            Logger.getLogger(MethodBlockNew.class.getName()).log(Level.SEVERE, null, e);
        }


        return result[0];

    }

}
