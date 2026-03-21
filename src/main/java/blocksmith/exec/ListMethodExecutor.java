package blocksmith.exec;

import blocksmith.domain.block.BlockDef;
import blocksmith.exec.BlockException.Severity;
import blocksmith.ui.graph.block.ExceptionPanel;
import blocksmith.ui.utils.ListUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public class ListMethodExecutor {

    private final BlockDef def;
    private final BlockFunc func;
    private final Deque<Integer> traversalLog;

    public ListMethodExecutor(BlockDef def, BlockFunc func) {
        this.def = def;
        this.func = func;

        this.traversalLog = new ArrayDeque<>(); // keep track which index of the list is currently being processed

    }

    public InvocationResult invoke(Object... paramaters) {
        return invokeListMethodArgs(traversalLog, paramaters);
    }

    public InvocationResult invoke2(Object a, Object b) {
        return invokeListMethodArgs2(traversalLog, a, b);
    }

    private InvocationResult invokeListMethodArgs(Deque<Integer> traversalLog, Object... parameters) {
        InvocationResult invocationResult = new InvocationResult();
        try {
//            Object result = method.invoke(null, parameters);
            Object result = func.apply(List.of(parameters));

            invocationResult.setData(result);

        } catch (Exception e) {
            Throwable throwable = e;
            if (e.getCause() != null) {
                throwable = e.getCause();
            }
            BlockException exception = new BlockException(getExceptionIndex(traversalLog), Severity.ERROR, throwable);
            invocationResult.exceptions().add(exception);
        }
        return invocationResult;
    }

    private InvocationResult invokeListMethodArgs2(Deque<Integer> traversalLog, Object a, Object b) {

        // both objects are single values
        if (!ListUtils.isList(b)) {
            return invokeListMethodArgs(traversalLog, a, b);

        } else { // object b is a list
            List<?> bList = (List<?>) b;
            List<Object> list = new ArrayList<>();
            InvocationResult invocationResult = new InvocationResult();
            invocationResult.setData(list);

            int i = 0;
            for (Object bItem : bList) {
                traversalLog.add(i);
                InvocationResult result = invokeListMethodArgs2(traversalLog, a, bItem);
                list.add(result.getData());
                invocationResult.exceptions().addAll(result.exceptions());
                traversalLog.pop();
            }

            return invocationResult;
        }
    }

    private String getExceptionIndex(Deque<Integer> traversalLog) {
        if (traversalLog.isEmpty()) {
            return null;
        }
        String result = "";
        for (Integer index : traversalLog.reversed()) {
            result += "[" + index + "]";
        }
        return result;
    }
}
