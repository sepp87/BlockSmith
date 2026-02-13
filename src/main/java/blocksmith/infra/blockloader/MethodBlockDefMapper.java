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

    public static BlockDef blockDefFromMethod(Method method) throws Exception {

        var metadata = method.getAnnotation(Block.class);
        var params = MethodParamDefMapper.paramDefsFromParameters(method);
        var inputs = MethodPortDefMapper.inputDefsFromParameters(method);
        var output = MethodPortDefMapper.outputDefFromReturnType(method);
        var isListOperator = isListOperator(method);

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
                List.of(output),
                isListOperator
        );
    }

    // TODO move somewhere else
    private static boolean isListOperator(Method method) {
        // If first input parameter is of type list, then this is a list operator block
        if (method.getParameters().length > 0 && List.class.isAssignableFrom(method.getParameters()[0].getType())) {
            return true;
        }
        return false;
    }
}
