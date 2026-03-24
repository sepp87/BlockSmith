package blocksmith.exec;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;
import blocksmith.app.logging.GraphLogFmt;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.value.Port;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class ExecutionEngine {

    private final static Logger LOGGER = Logger.getLogger(ExecutionEngine.class.getName());

    private final BlockDefLibrary defLibrary;
    private final BlockFuncLibrary funcLibrary;

    public ExecutionEngine(BlockDefLibrary defLibrary, BlockFuncLibrary funcLibrary) {
        this.defLibrary = defLibrary;
        this.funcLibrary = funcLibrary;
    }

    public void runAll(Graph current, ExecutionState state) {
        var sinks = resolveSinksOf(current);

        for (var sink : sinks) {
            run(sink, current, state);
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

    private void run(BlockId id, Graph current, ExecutionState state) {

        try {
            var block = current.block(id).orElseThrow();
            var inputs = collectInputValues(current, state, block);
            state.updateBlockState(id, inputs.byRef(), BlockStatus.RUNNING, List.of());
            var output = runBlock(block, inputs.byIndex());
            state.updateBlockState(id, output.values(), BlockStatus.FINISHED, output.exceptions());

        } catch (RuntimeException ex) {
            var critical = BlockException.critical(ex);
            state.updateBlockState(id, Map.of(), BlockStatus.FAILED, List.of(critical));
            LOGGER.severe(ex.getMessage());
        }

    }

    private record ResolvedValues(List<Object> byIndex, Map<PortRef, Object> byRef) {

    }

    private ResolvedValues collectInputValues(Graph current, ExecutionState state, Block block) {

        var byIndex = new TreeMap<Integer, Object>();
        var byRef = new HashMap<PortRef, Object>();

        var inputs = block.inputPorts();
        var params = block.params();

        for (var input : inputs) {
            var index = input.argIndex();
            var ref = PortRef.input(block.id(), input.valueId());
            var value = resolveInputValueOf(current, state, block, input);
//            System.out.println("Port received " + GraphLogFmt.block(block.id()) + "." + input.valueId() + " = " + String.valueOf(value));
            byIndex.put(index, value);
            byRef.put(ref, value);
        }

        for (var param : params) {
            var index = param.argIndex();
            var ref = PortRef.input(block.id(), param.valueId());
            var value = param.value();
            byIndex.put(index, value);
            byRef.put(ref, value);
        }

        return new ResolvedValues(new ArrayList<>(byIndex.values()), byRef);
    }

    private Object resolveInputValueOf(Graph current, ExecutionState state, Block block, Port input) {

        var inputRef = PortRef.input(block.id(), input.valueId());

        if (GraphLogFmt.block(block.id()).equals("411de2cd")) {
            System.out.println("state.hasValueOf(inputRef) " + state.hasValueOf(inputRef));
        }

        if (state.hasValueOf(inputRef)) {
            return state.valueOf(inputRef);
        }

        // retrieve upstream - case: unconnected
        var connection = current.incomingConnection(inputRef);
        if (GraphLogFmt.block(block.id()).equals("411de2cd")) {
            System.out.println("connection.isEmpty() " + connection.isEmpty());
        }

        if (connection.isEmpty()) {
            return null;
        }

        // retrieve upstream - case: connected and provides value
        var connectedOutput = connection.get().from();
        var connectedBlock = connectedOutput.blockId();
        if (GraphLogFmt.block(block.id()).equals("411de2cd")) {
            System.out.println("state.statusOf(connectedBlock) " + state.statusOf(connectedBlock));
            System.out.println("state.hasValueOf(connectedOutput) " + state.hasValueOf(connectedOutput));
            System.out.println("state.valueOf(connectedOutput) " + state.valueOf(connectedOutput));
        }

//        if (state.statusOf(connectedBlock) == BlockStatus.FINISHED) {
        if (state.hasValueOf(connectedOutput)) {
            // TODO convert effective values if needed e.g. single to list, path to file, file to path
            var converted = state.valueOf(connectedOutput);
            return converted;
        }

        if (state.statusOf(connectedBlock) == BlockStatus.RUNNING) {
            return null;
            // TBD cycle detected?
//            throw new RuntimeException("Execution process interrupted, because connected upstream block yielded a severe runtime exception.");
        }

        if (GraphLogFmt.block(block.id()).equals("411de2cd")) {
            System.out.println("run(connectedBlock, current, state);");
        }

        // retrieve upstream - case: connected, but not yet executed
        run(connectedBlock, current, state);
        // TODO convert effective values if needed e.g. single to list, path to file, file to path
        // TODO collect blocks and execute concurrently
        if (GraphLogFmt.block(block.id()).equals("411de2cd")) {
            System.out.println("state.statusOf(connectedBlock) " + state.statusOf(connectedBlock));
        }

        if (state.statusOf(connectedBlock) == BlockStatus.FINISHED) {
            var converted = state.valueOf(connectedOutput);
            return converted;
//            throw new RuntimeException("Execution process interrupted, because connected upstream block yielded a severe runtime exception.");
        }
        return null;
    }

    private ExecutionResult runBlock(Block block, List<Object> inputValues) {

        var def = defLibrary.resolve(block.type())
                .orElseThrow(() -> new RuntimeException("Execution process interrupted, block def could NOT be resolved"));
        var func = funcLibrary.resolve(block.type())
                .orElseThrow(() -> new RuntimeException("Execution process interrupted, block func could NOT be resolved")); // TODO set exception to state if none found

        return new BlockExecutor(block.id(), def, func, true).invoke(inputValues.toArray());
    }
}
