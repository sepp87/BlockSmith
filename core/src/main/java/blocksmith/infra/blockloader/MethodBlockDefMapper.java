package blocksmith.infra.blockloader;

import blocksmith.domain.block.BlockDef;
import java.lang.reflect.Method;
import java.util.List;
import blocksmith.infra.blockloader.annotations.Block;

/**
 *
 * @author joost
 */
public class MethodBlockDefMapper {

    public static BlockDef map(Method method) throws Exception {

        var metadata = method.getAnnotation(Block.class);
        var params = MethodParamMapper.map(method);
        var inputs = MethodInputMapper.map(method);
        var outputs = MethodOutputMapper.map(method);
        var hasAggregatedInput = method.isVarArgs();

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
