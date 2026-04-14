package blocksmith.exec;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockExecLibrary;
import blocksmith.app.logging.GraphLogFmt;
import blocksmith.domain.block.ArrayBlock;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.ValueTypeResolver;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.ValueType.SimpleType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class ExecutionEngine {

    private final static Logger LOGGER = Logger.getLogger(ExecutionEngine.class.getName());

    private final BlockDefLibrary defLibrary;
    private final BlockExecLibrary execLibrary;
    private final SourceBlockIndex sourceBlocks;

    public ExecutionEngine(BlockDefLibrary defLibrary, BlockExecLibrary execLibrary, SourceBlockIndex sourceBlocks) {
        this.defLibrary = defLibrary;
        this.execLibrary = execLibrary;
        this.sourceBlocks = sourceBlocks;
    }

    public void runAll(Graph current, ExecutionState state, BiConsumer<BlockId, Map<PortRef, Object>> onSourceBlockEmitted) {
        var sinks = resolveSinksOf(current);

        for (var sink : sinks) {
            run(sink, current, state, onSourceBlockEmitted);
        }

        // identify subgraphs and group blocks accordingly 
        // identify sink blocks of subgraphs
        // execute subgraphs concurrently
        // check if block status is AWAITING/PENDING/IDLE
        // execute blocks (if IDLE) one after the other
    }

    private Collection<BlockId> resolveSinksOf(Graph current) {
        var result = new HashSet<BlockId>();
        for (var block : current.blocks()) {
            if (current.hasOutgoingConnections(block.id())) {
                continue;
            }
            result.add(block.id());
        }
        return result;
    }

    private void run(BlockId id, Graph current, ExecutionState state, BiConsumer<BlockId, Map<PortRef, Object>> onSourceBlockEmitted) {

        try {
            var block = current.block(id).orElseThrow();
            var inputs = collectInputValues(current, state, block, onSourceBlockEmitted);
            state.updateBlockState(id, inputs.byRef(), BlockStatus.RUNNING, List.of());
            var output = runBlock(block, inputs.byIndex(), onSourceBlockEmitted);
            state.updateBlockState(id, output.values(), BlockStatus.FINISHED, output.exceptions());

        } catch (RuntimeException ex) {
            var critical = BlockException.critical(ex);
            state.updateBlockState(id, Map.of(), BlockStatus.FAILED, List.of(critical));
            LOGGER.log(Level.SEVERE, ex.getMessage());
            ex.printStackTrace();
        }

    }

    private record ResolvedValues(List<Object> byIndex, Map<PortRef, Object> byRef) {

    }

    private ResolvedValues collectInputValues(Graph current, ExecutionState state, Block block, BiConsumer<BlockId, Map<PortRef, Object>> onSourceBlockEmitted) {

        var byIndex = new TreeMap<Integer, Object>();
        var byRef = new HashMap<PortRef, Object>();

        var inputs = block.inputPorts();

        if (block instanceof ArrayBlock arrayBlock) {
            inputs = arrayBlock.fixedInputPorts();

            var elements = arrayBlock.connectedElements();
            var any = arrayBlock.anyElement();
            var index = any.argIndex();
            var ref = PortRef.input(block.id(), any.valueId().split("#")[0]);

            var values = new ArrayList<Object>();
            for (var element : elements) {
                var value = resolveInputValueOf(current, state, block, element, onSourceBlockEmitted);
                values.add(value);
                LOGGER.log(Level.FINEST, GraphLogFmt.block(block.id()) + "." + element.valueId() + " = " + String.valueOf(value));
            }

            var valueType = ValueTypeResolver.typeOf(current, PortRef.input(block.id(), any.valueId()));
            var rawType = valueType instanceof SimpleType simple ? simple.raw() : Object.class;
            var size = values.size();
            var array = Array.newInstance(rawType, size);
            for (int i = 0; i < size; i++) {
                Array.set(array, i, values.get(i));
            }
            byIndex.put(index, array);
            byRef.put(ref, array);
        }

        for (var input : inputs) {
            var index = input.argIndex();
            var ref = PortRef.input(block.id(), input.valueId());
            var value = resolveInputValueOf(current, state, block, input, onSourceBlockEmitted);
            LOGGER.log(Level.FINEST, GraphLogFmt.block(block.id()) + "." + input.valueId() + " = " + String.valueOf(value));

            byIndex.put(index, value);
            byRef.put(ref, value);
        }

        var params = block.params();

        for (var param : params) {
            var index = param.argIndex();
            var ref = PortRef.input(block.id(), param.valueId());
            var value = param.value();
            byIndex.put(index, value);
            byRef.put(ref, value);
        }

        return new ResolvedValues(new ArrayList<>(byIndex.values()), byRef);
    }

    private Object resolveInputValueOf(Graph current, ExecutionState state, Block block, Port input, BiConsumer<BlockId, Map<PortRef, Object>> onSourceBlockEmitted) {

        var inputRef = PortRef.input(block.id(), input.valueId());

        if (state.hasValueOf(inputRef)) {
            return state.valueOf(inputRef);
        }

        // retrieve upstream - case: unconnected
        var connection = current.incomingConnection(inputRef);

        if (connection.isEmpty()) {
            return null;
        }

        // retrieve upstream - case: connected and provides value
        var connectedOutput = connection.get().from();
        var connectedBlock = connectedOutput.blockId();

        if (state.hasValueOf(connectedOutput)) {
            // TODO convert effective values if needed e.g. single to list, path to file, file to path
            var value = state.valueOf(connectedOutput);
            var converted = ValueConverter.convert(value, connectedOutput, inputRef, current);
            return converted;
        }

        if (state.statusOf(connectedBlock) == BlockStatus.RUNNING) {
            return null;
            // TBD cycle detected?
//            throw new RuntimeException("Execution process interrupted, because connected upstream block yielded a severe runtime exception.");
        }

        // retrieve upstream - case: connected, but not yet executed
        run(connectedBlock, current, state, onSourceBlockEmitted);
        // TODO convert effective values if needed e.g. single to list, path to file, file to path
        // TODO collect blocks and execute concurrently

        if (state.statusOf(connectedBlock) == BlockStatus.FINISHED) {
            var value = state.valueOf(connectedOutput);
            var converted = ValueConverter.convert(value, connectedOutput, inputRef, current);
            return converted;
//            throw new RuntimeException("Execution process interrupted, because connected upstream block yielded a severe runtime exception.");
        }
        return null;
    }

    private ExecutionResult runBlock(Block block, List<Object> inputValues, BiConsumer<BlockId, Map<PortRef, Object>> onSourceBlockEmitted) {

        var def = defLibrary.resolve(block.type())
                .orElseThrow(() -> new RuntimeException("Execution process interrupted, block def could NOT be resolved"));
        var exec = execLibrary.resolve(block.type())
                .orElseThrow(() -> new RuntimeException("Execution process interrupted, block func could NOT be resolved")); // TODO set exception to state if none found

        return switch (exec) {

            case BlockFunc func ->
                new BlockRunner(block.id(), def, func).invoke(inputValues.toArray());

            case SourceBlockSpec spec -> {
                var source = sourceBlocks.get(block.id()).orElseThrow();

                if (spec.injector() != null) {
                    spec.injector().accept(source, inputValues);
                }

                if (!source.isRunning()) {
                    source.start(
                            (result) -> {
                                var outputs = def.outputExtractor().extract(block.id(), result);
                                onSourceBlockEmitted.accept(block.id(), outputs);
                            }
                    );
                }
                // exception is thrown to interrupt the execution recursion, since source blocks do not create an immediate output
                throw new RuntimeException("Temp circuit breaker for source blocks");
//                yield new ExecutionResult(Map.of(), List.of());
            }

        };

    }

}
