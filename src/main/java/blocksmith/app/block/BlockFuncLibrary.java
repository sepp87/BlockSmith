package blocksmith.app.block;

import blocksmith.exec.BlockFunc;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author joost
 */
public class BlockFuncLibrary {
    
  private final Map<String, BlockFunc> byType;

    public BlockFuncLibrary(Map<String, BlockFunc> blockFuncs) {
        this.byType = Map.copyOf(blockFuncs);
    }
    
    public Optional<BlockFunc> findByType(String type) {
        return Optional.ofNullable(byType.get(type));
    }    
}
