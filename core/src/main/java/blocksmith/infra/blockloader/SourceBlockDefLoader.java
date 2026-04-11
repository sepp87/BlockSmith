package blocksmith.infra.blockloader;

import blocksmith.app.outbound.BlockDefLoader;
import blocksmith.domain.block.BlockDef;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class SourceBlockDefLoader implements BlockDefLoader {

    private static final Logger LOGGER = Logger.getLogger(SourceBlockDefLoader.class.getName());

    private final Collection<SourceBlockInspector> inspectors;

    public SourceBlockDefLoader(Collection<SourceBlockInspector> inspectors) {
        this.inspectors = inspectors;
    }

    public Collection<BlockDef> load() {
        return from(inspectors);
    }

    public static List<BlockDef> from(Collection<SourceBlockInspector> inspectors) {
        var result = new ArrayList<BlockDef>();

        for (var inspector : inspectors) {
            try {
                var def = SourceBlockDefMapper.map(inspector);
                result.add(def);

            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Block definition failed to load for block \"{0}\". {1}", new Object[]{inspector.metadata().type(), ex.getMessage()});
            }
        }

        return result;
    }
}
