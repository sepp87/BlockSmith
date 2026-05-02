package blocksmith.app.outbound;

import blocksmith.exec.block.BlockExec;
import java.util.Map;

/**
 *
 * @author joost
 */
public interface BlockExecLoader {

    Map<String, BlockExec> load() ;
}
