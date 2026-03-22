package blocksmith.exec;

import blocksmith.app.block.BlockDefLibrary;
import blocksmith.app.block.BlockFuncLibrary;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.value.Port;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class FlowEngine {
    
    private final static Logger LOGGER = Logger.getLogger(FlowEngine.class.getName());
    
    private final BlockDefLibrary defLibrary;
    private final BlockFuncLibrary funcLibrary;
    
    public FlowEngine(BlockDefLibrary defLibrary, BlockFuncLibrary funcLibrary) {
        this.defLibrary = defLibrary;
        this.funcLibrary = funcLibrary;
    }
    
    public void runAll(Graph current, ForgeState state) {
        // identify subgraphs and group blocks accordingly 
        // identify sink blocks of subgraphs
        // execute subgraphs concurrently
        // check if block status is AWAITING/PENDING/IDLE
        // execute blocks (if IDLE) one after the other
    }
    
    public void run(BlockId id, Graph current, ForgeState state) {
        var block = current.block(id).orElseThrow();
        
        try {
            var inputs = collectInputValues(current, state, block);

            // TBD or set runing in runBlock()
            state.updateBlockState(id, inputs.byRef(), ExecutionStatus.RUNNING, List.of());
            var outputs = runBlock(block, inputs.byIndex(), state);

            // TODO block executor should return results as map of values (data + exceptions), indexed by PortRef
            // TBD or update state directly in runBlock()
            var values = new HashMap<PortRef, Object>();
            values.put(PortRef.output(block.id(), "value"), outputs.getData());
            state.updateBlockState(id, values, ExecutionStatus.FINISHED, outputs.exceptions());
            
        } catch (RuntimeException ex) {
            LOGGER.severe(ex.getMessage());
        }
        
    }
    
    private record ResolvedValues(List<Object> byIndex, Map<PortRef, Object> byRef) {
        
    }
    
    private ResolvedValues collectInputValues(Graph current, ForgeState state, Block block) {
        
        var byIndex = new TreeMap<Integer, Object>();
        var byRef = new HashMap<PortRef, Object>();
        
        var inputs = block.inputPorts();
        var params = block.params();
        
        for (var input : inputs) {
            var index = input.argIndex();
            var ref = PortRef.input(block.id(), input.valueId());
            var value = resolveInputValueOf(current, state, block, input);
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
        
        return new ResolvedValues(List.copyOf(byIndex.values()), byRef);
    }
    
    private Object resolveInputValueOf(Graph current, ForgeState state, Block block, Port input) {
        
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
        if (state.hasValueOf(connectedOutput)) {
            // TODO convert effective values if needed e.g. single to list, path to file, file to path
            var converted = state.valueOf(connectedOutput);
            return converted;
        }

        // retrieve upstream - case: connected, but not yet executed
        var connectedBlock = connectedOutput.blockId();
        run(connectedBlock, current, state);
        // TODO convert effective values if needed e.g. single to list, path to file, file to path
        // TODO collect blocks and execute concurrently
        // TBD read value directly from running block, or retrieve value from state, since it is set to state within runBlock()
        if (!state.exceptionsOf(connectedBlock).isEmpty()) {
            throw new RuntimeException("Run process interrupted, because connected upstream block yielded an exception.");
        }
        return state.valueOf(connectedOutput);
    }
    
    private ForgeResult runBlock(Block block, List<Object> inputValues, ForgeState state) {
        
        var def = defLibrary.resolve(block.type()).orElseThrow();
        var func = funcLibrary.resolve(block.type()).orElseThrow(); // TODO set exception to state if none found

        return new BlockExecutor(def, func, true).invoke(inputValues.toArray());
    }
}
