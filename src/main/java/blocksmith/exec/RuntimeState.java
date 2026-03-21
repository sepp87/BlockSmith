package blocksmith.exec;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * @author joost
 */
public class RuntimeState {

    private final Map<PortRef, Object> valueIndex = new HashMap<>();
    private final Map<BlockId, ExecutionStatus> blockStatus = new HashMap<>();
    private final Map<BlockId, List<BlockException>> blockExceptions = new HashMap<>();

    private final List<Consumer<BlockId>> listeners = new ArrayList<>();

    public void setOnBlockUpdated(Consumer<BlockId> listener) {
        listeners.clear();
        listeners.add(listener);
    }

    public void setBlockRunning(BlockId block) {
        blockStatus.put(block, ExecutionStatus.RUNNING);
        blockExceptions.remove(block);
        blockUpdated(block);
    }

    public void updateBlockState(
            BlockId block,
            Map<PortRef, Object> values,
            ExecutionStatus status,
            List<BlockException> exceptions) {

        valueIndex.putAll(values);
        blockStatus.put(block, status);
        blockExceptions.put(block, exceptions);
        blockUpdated(block);
    }

    private void blockUpdated(BlockId block) {
        listeners.forEach(c -> c.accept(block));
    }

    public Map<PortRef, Object> valuesOf(BlockId block) {
        return valueIndex.entrySet().stream()
                .filter(e -> e.getKey().blockId().equals(block))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public ExecutionStatus statusOf(BlockId block) {
        return blockStatus.getOrDefault(block, ExecutionStatus.DIRTY);
    }

    public List<BlockException> exceptionsOf(BlockId block) {
        return blockExceptions.getOrDefault(block, List.of());
    }
}
