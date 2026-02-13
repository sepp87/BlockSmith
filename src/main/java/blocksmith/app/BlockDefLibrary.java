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
        blockDefs.forEach(def -> {
            byType.put(def.type(), def);
            for (var alias : def.aliases()) {
                byAlias.put(alias, def);
            }
        });

    }

    public Optional<BlockDef> resolve(String type) {
        return Optional.ofNullable(byType.getOrDefault(type, byAlias.get(type)));
    }

    public Collection<String> types() {
        return Stream.concat(byType.keySet().stream(), byAlias.keySet().stream()).toList();
    }
}
