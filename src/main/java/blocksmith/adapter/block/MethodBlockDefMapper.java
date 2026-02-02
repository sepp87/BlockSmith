package blocksmith.adapter.block;

import blocksmith.domain.block.BlockDef;
import btscore.graph.block.BlockMetadata;
import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * @author joost
 */
public class MethodBlockDefMapper {

    public static BlockDef blockDefFromMethod(Method method) throws Exception {

        var metadata = method.getAnnotation(BlockMetadata.class);
        var inputs = MethodPortDefMapper.inputDefsFromParameters(method);
        var output = MethodPortDefMapper.outputDefFromReturnType(method);
        var params = MethodParamDefMapper.paramDefsFromParameters(method);
        var isListOperator = isListOperator(method);

        return new BlockDef(
                metadata,
                inputs,
                List.of(output),
                params,
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
