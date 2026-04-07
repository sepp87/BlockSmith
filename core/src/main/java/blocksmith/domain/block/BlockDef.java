package blocksmith.domain.block;

import blocksmith.domain.value.ParamDef;
import blocksmith.domain.value.PortDef;
import java.util.List;
import blocksmith.utils.icons.FontAwesomeIcon;

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
        OutputExtractor outputExtractor,
        boolean hasAggregatedInput) {

}
