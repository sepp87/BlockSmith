package blocksmith.app.ports;

import blocksmith.exec.BlockFunc;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author joost
 */
public interface BlockFuncLoader {

    Map<String, BlockFunc> load() ;
}
