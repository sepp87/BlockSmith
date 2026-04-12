
package blocksmith.infra.blockloader;

import blocksmith.app.outbound.BlockExecLoader;
import blocksmith.exec.BlockExec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joost
 */
public class CompositeBlockExecLoader implements BlockExecLoader {

        private final List<BlockExecLoader> loaders;

    public CompositeBlockExecLoader(BlockExecLoader... loaders) {
        this.loaders = List.of(loaders);
    }

    public Map<String, BlockExec> load() {
        var result = new HashMap<String, BlockExec>();

        for (var loader : loaders) {
            result.putAll(loader.load());
        }

        return result;
    }

}
