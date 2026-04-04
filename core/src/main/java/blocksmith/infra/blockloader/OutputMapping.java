
package blocksmith.infra.blockloader;

import blocksmith.domain.block.OutputExtractor;
import blocksmith.domain.value.PortDef;
import java.util.List;

/**
 *
 * @author joost
 */
public record OutputMapping (
        List<PortDef> ports,
        OutputExtractor extractor
        ) {
 
    
}
