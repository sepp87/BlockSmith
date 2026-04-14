package blocksmith.infra.blockloader;

import blocksmith.domain.block.BlockDef;
import blocksmith.domain.value.ParamDef;
import blocksmith.domain.value.PortDef;
import java.util.List;

/**
 *
 * @author joost
 */
public class SourceBlockDefMapper {


    public static BlockDef map(SourceBlockInspector clazz) throws ReflectiveOperationException, Exception {
    
        var inputMethod = clazz.inputMethod().orElse(null);
        var outputMethod = clazz.outputMethod().orElse(null);
        
        var metadata = clazz.metadata();
        var params = inputMethod == null ? List.<ParamDef>of() : MethodParamMapper.map(inputMethod);
        var inputs = inputMethod == null ? List.<PortDef>of() : MethodInputMapper.map(inputMethod);
        var outputs = outputMethod == null ? OutputMapping.empty() : MethodOutputMapper.map(outputMethod);
        var hasAggregatedInput = inputMethod == null ? false : inputMethod.isVarArgs();

        return new BlockDef(
                metadata.type(),
                metadata.name(),
                metadata.description(),
                metadata.category(),
                List.of(metadata.tags()),
                List.of(metadata.aliases()),
                metadata.icon(),
                params,
                inputs,
                outputs.ports(),
                outputs.extractor(),
                hasAggregatedInput
        );

    }

}
