package blocksmith.domain.block;

import btscore.graph.block.BlockMetadata;
import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public record BlockDef(
        BlockMetadata metadata,
        List<PortDef> inputs,
        List<PortDef> outputs,
        List<ParamDef> params,
        boolean isListOperator) {

}
