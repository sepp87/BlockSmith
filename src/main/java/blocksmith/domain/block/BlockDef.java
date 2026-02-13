package blocksmith.domain.block;

import blocksmith.domain.value.ParamDef;
import blocksmith.domain.value.PortDef;
import java.util.List;
import blocksmith.infra.blockloader.annotations.Block;
import btscore.icons.FontAwesomeIcon;

/**
 *
 * @author joostmeulenkamp
 */
public record BlockDef(
        String type,
        String name, 
        String description,
        String category,
        List<String> tags,
        List<String> aliases,
        FontAwesomeIcon icon,
        List<ParamDef> params,
        List<PortDef> inputs,
        List<PortDef> outputs,
        boolean isListOperator) {

}
