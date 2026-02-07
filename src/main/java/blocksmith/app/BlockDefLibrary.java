package blocksmith.app;

import blocksmith.domain.block.BlockDef;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 *
 * @author joost
 */
public class BlockDefLibrary {

    private final Map<String, BlockDef> byType = new TreeMap<>();
    private final Map<String, BlockDef> byAlias = new TreeMap<>();

    public BlockDefLibrary(Collection<BlockDef> blockDefs) {
        blockDefs.forEach(e -> byType.put(e.metadata().type(), e));
        blockDefs.forEach(e -> {
            for (var alias : e.metadata().aliases()) {
                byAlias.put(alias, e);
            }
        });

    }

    public Optional<BlockDef> findByType(String type) {
        return Optional.ofNullable(byType.getOrDefault(type, byAlias.get(type)));
    }

    public Collection<String> types() {
        return Stream.concat(byType.keySet().stream(), byAlias.keySet().stream()).toList();
    }
}
