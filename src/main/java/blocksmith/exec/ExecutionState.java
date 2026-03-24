package blocksmith.exec;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author joost
 */
public class ExecutionState {

    private final Map<PortRef, Object> valueIndex = new HashMap<>();
    private final Map<BlockId, BlockStatus> blockStatus = new HashMap<>();
    private final Map<BlockId, List<BlockException>> blockExceptions = new HashMap<>();

    private final List<Consumer<BlockId>> listeners = new ArrayList<>();

    public void setValueOf(PortRef ref, Object value) {
        valueIndex.put(ref, value);
    }
    
    public boolean removeValueOf(PortRef ref) {
        return valueIndex.remove(ref) != null;
    }
    
    public void removeStatusOf(BlockId block) {
        blockStatus.remove(block);
    }
    
    public void clearExceptionsOf(BlockId block) {
        blockExceptions.remove(block);
    }
    
    public void setOnBlockUpdated(Consumer<BlockId> listener) {
        listeners.clear();
        listeners.add(listener);
    }

    public void setBlockRunning(BlockId block) {
        blockStatus.put(block, BlockStatus.RUNNING);
        blockExceptions.remove(block);
        blockUpdated(block);
    }

    public void updateBlockState(
            BlockId block,
            Map<PortRef, Object> values,
            BlockStatus status,
            List<BlockException> exceptions) {

        valueIndex.putAll(values);
        blockStatus.put(block, status);
        blockExceptions.put(block, exceptions);
        blockUpdated(block);
    }

    private void blockUpdated(BlockId block) {
//        System.out.println("BLOCK UPDATED IN EXECUTION STATE");
        listeners.forEach(c -> c.accept(block));
    }

    public boolean hasValueOf(PortRef port) {
        return valueIndex.containsKey(port);
    }
    
    
    public Object valueOf(PortRef port) {
        return valueIndex.get(port);
    }
    
    public Map<PortRef, Object> valuesOf(BlockId block) {
        var result = new HashMap<PortRef, Object>();
        valueIndex.entrySet().forEach(e -> {
            var indexed = e.getKey().blockId();
            if (indexed.equals(block)) {
                result.put(e.getKey(), e.getValue());
            }
        });
        return result; // do NOT return Map.copyOf() since the result can hold keys with null values
    }

    public BlockStatus statusOf(BlockId block) {
        return blockStatus.getOrDefault(block, BlockStatus.IDLE);
    }

    public List<BlockException> exceptionsOf(BlockId block) {
        return blockExceptions.getOrDefault(block, List.of());
    }
}
