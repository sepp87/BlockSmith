package blocksmith.infra.blockloader.extractor;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.OutputExtractor;
import blocksmith.domain.connection.PortRef;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author joost
 */
public class SingleOutputExtractor implements OutputExtractor {

    private final String valueId;

    public SingleOutputExtractor(String valueId) {
        this.valueId = valueId;

    }

    public Map<PortRef, Object> extract(BlockId block, Object result) {
        var byPort = new HashMap<PortRef, Object>();
        byPort.put(PortRef.output(block, valueId), result);
        return byPort;
    }
}
