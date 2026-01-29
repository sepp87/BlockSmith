package blocksmith.app;

import blocksmith.domain.block.BlockDef;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 *
 * @author joost
 */
public class BlockDefLibrary {

    private final Map<String, BlockDef> byType = new TreeMap<>();

    public BlockDefLibrary(Collection<BlockDef> blockDefs) {
        blockDefs.forEach(e -> byType.put(e.metadata().type(), e));
    }
    
    public Optional<BlockDef> findByType(String type) {
        return Optional.ofNullable(byType.get(type));
    }
    
    public Collection<String> types() {
        return byType.keySet();
    }
}
