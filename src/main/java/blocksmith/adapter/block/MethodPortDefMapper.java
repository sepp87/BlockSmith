package blocksmith.adapter.block;

import blocksmith.domain.block.Port;
import blocksmith.domain.block.PortDef;
import btscore.graph.port.AutoConnectable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import blocksmith.domain.block.Value;
import static blocksmith.domain.block.Value.Source.PARAM;

/**
 *
 * @author joost
 */
public class MethodPortDefMapper {

    public static List<PortDef> inputDefsFromParameters(Method method) {
        var result = new ArrayList<PortDef>();

        int i = 0;
        for (Parameter p : method.getParameters()) {
            boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(p.getType());
            Value value = p.getAnnotation(Value.class);
            boolean hasAnnotation = value != null;

            try {
                // by default if:
                // - value annotation missing > source of value is port by default
                // - value annotation present > source of value is param by default
                if (hasAnnotation && value.source().equals(PARAM)) {
                    continue;

                } else {
                    var valueType = ValueTypeMappingUtils.fromMethodParameter(p);
                    var portDef = new PortDef(
                            i,
                            p.getName(),
                            Port.Direction.INPUT,
                            valueType,
                            isAutoConnectable);

                    result.add(portDef);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "INPUT of block definition unknown: "
                        + method.getClass().getSimpleName() + "."
                        + method.getName() + "() "
                        + p.getName(), e);
            }

            i++;
        }
        return result;
    }

    public static PortDef outputDefFromReturnType(Method method) throws ReflectiveOperationException {
        Class<?> returnType = method.getReturnType();

        boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(returnType);

        try {
            var valueType = ValueTypeMappingUtils.fromType(returnType);
            var portDef = new PortDef(
                    -1,
                    returnType.getSimpleName(),
                    Port.Direction.OUTPUT,
                    valueType,
                    isAutoConnectable
            );

            return portDef;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "OUTPUT of block definition unknown: "
                    + method.getClass().getSimpleName() + "."
                    + method.getName() + "() "
                    + returnType.getSimpleName(), e);
        }

    }

}
