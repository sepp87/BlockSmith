package blocksmith.app.outbound;

import blocksmith.domain.block.BlockDef;
import java.util.Collection;

/**
 *
 * @author joostmeulenkamp
 */
public interface BlockDefLoader {

    Collection<BlockDef> load();


}
