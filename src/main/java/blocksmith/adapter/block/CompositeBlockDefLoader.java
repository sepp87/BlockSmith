package blocksmith.adapter.block;

import blocksmith.app.BlockDefLoader;
import blocksmith.domain.block.BlockDef;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joost
 */
public class CompositeBlockDefLoader implements BlockDefLoader {

    private final List<BlockDefLoader> loaders;

    public CompositeBlockDefLoader(List<BlockDefLoader> loaders) {
        this.loaders = List.copyOf(loaders);
    }

    public Collection<BlockDef> load() {
        var result = new ArrayList<BlockDef>();

        for (var loader : loaders) {
            result.addAll(loader.load());
        }

        return result;
    }

}
