package blocksmith.app.block;

import blocksmith.exec.BlockExec;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author joost
 */
public class BlockExecLibrary {
    
  private final Map<String, BlockExec> byType;

    public BlockExecLibrary(Map<String, BlockExec> blockFuncs) {
        this.byType = Map.copyOf(blockFuncs);
    }
    
    public Optional<BlockExec> resolve(String type) {
        return Optional.ofNullable(byType.get(type));
    }    
}
