package blocksmith.exec;

import blocksmith.domain.block.BlockDef;
import blocksmith.domain.value.ValueDef;
import blocksmith.domain.value.ValueType;
import blocksmith.ui.utils.ListUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author joost
 */
public class UnifiedMethodExecutor {

    private final BlockDef def;
    private final BlockFunc func;

    public UnifiedMethodExecutor(BlockDef def, BlockFunc func) {
        this.def = def;
        this.func = func;
    }

    public InvocationResult execute(Object... args) {
        var traversalLog = new ArrayDeque<Integer>();
        return execute(traversalLog, args);
    }

    // TODO actually sort by argIndex
    private List<? extends ValueDef> valuesSortedByArgIndex() {
        return Stream.concat(def.inputs().stream(), def.params().stream()).toList();

    }

    private InvocationResult execute(Deque<Integer> traversalLog, Object... args) {
        var values = valuesSortedByArgIndex();

        if (values.size() != args.length) {
            System.out.println("Number of args did NOT match the number parameters. TODO lacing?");
            throw new IllegalArgumentException("Number of args did NOT match the number parameters. TODO lacing?");
        }

        var evalArgs = new ArrayList<Object>();
        var definedSingular = 0;
        for (int i = 0; i < args.length; i++) {
            var input = values.get(i);
            if (input.valueType() instanceof ValueType.ListType) {
                continue;
            }
            definedSingular++;
            evalArgs.add(args[i]);
        }
        int numOflistArgs = ListUtils.getListCount(evalArgs.toArray());

        // all args defined as singular are effectively singular
        if (numOflistArgs == 0) {
            return executeFunc(traversalLog, args);
        }

        // all args defined as singular are effectively plurar
        long shortestListSize = ListUtils.getShortestListSize(evalArgs);
        if (numOflistArgs == definedSingular) {
            return loopAndExecute(traversalLog, shortestListSize, args);
        }

        // some args defined as singular are effectively plurar
        if (shortestListSize == 0) {
            return null;
        }
        args = argsToList(shortestListSize, args);
        return loopAndExecute(traversalLog, shortestListSize, args);

    }

    private Object[] argsToList(long shortestListSize, Object... args) {
        var values = valuesSortedByArgIndex();
        var length = values.size();

        for (int i = 0; i < length; i++) {
            Object p = args[i];
            if (ListUtils.isList(p)) {
                continue;
            }
            List<Object> list = new ArrayList<>();
            for (int j = 0; j < shortestListSize; j++) {
                list.add(p);
            }
            args[i] = list;
        }
        return args;
    }

    private InvocationResult loopAndExecute(Deque<Integer> traversalLog, long shortestListSize, Object... args) {
        List<Object> list = new ArrayList<>();
        InvocationResult invocationResult = new InvocationResult();
        invocationResult.setData(list);

        for (int i = 0; i < shortestListSize; i++) {
            traversalLog.add(i);

            Object[] argBatch = prepArgBatch(i, args);

            InvocationResult subResult = execute(traversalLog, argBatch);
            invocationResult.exceptions().addAll(subResult.exceptions());
            list.add(subResult.getData());

            traversalLog.pop();
        }
        return invocationResult;
    }

    private Object[] prepArgBatch(int batchNumber, Object... args) {
        var values = valuesSortedByArgIndex();
        var length = values.size();

        Object[] batch = new Object[length];
        for (int argIndex = 0; argIndex < length; argIndex++) {

            Object arg;
            if (values.get(argIndex).valueType() instanceof ValueType.ListType) { // if value is defined as list, the list is the arg
                arg = args[argIndex];

            } else {
                List<?> listArg = (List<?>) args[argIndex];
                arg = listArg.get(batchNumber);

            }
            batch[argIndex] = arg;

        }
        return batch;

    }

    private InvocationResult executeFunc(Deque<Integer> traversalLog, Object... args) {
        InvocationResult invocationResult = new InvocationResult();

        try {
            Object result = func.apply(Arrays.asList(args)); // Arrays.asList allows nulls but prevents structural mutation (by design)
            invocationResult.setData(result);

        } catch (Exception e) {
            e.printStackTrace();
            Throwable throwable = e;
            if (e.getCause() != null) {
                throwable = e.getCause();
            }
            BlockException exception = new BlockException(getIndexLabel(traversalLog), BlockException.Severity.ERROR, throwable);
            invocationResult.exceptions().add(exception);
        }
        return invocationResult;
    }

    private String getIndexLabel(Deque<Integer> traversalLog) {
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
