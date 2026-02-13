package blocksmith.infra.blockloader;

import blocksmith.domain.value.Port;
import blocksmith.domain.value.PortDef;
import btscore.graph.port.AutoConnectable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import blocksmith.infra.blockloader.annotations.Value;
import static blocksmith.infra.blockloader.annotations.Value.Source.PARAM;

/**
 *
 * @author joost
 */
public class MethodPortDefMapper {

    public static List<PortDef> inputDefsFromParameters(Method method) {
        var result = new ArrayList<PortDef>();

        int argIndex = 0;
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
                    var valueId = value != null && !value.id().isEmpty() ? value.id() : p.getName();
                    
                    var portDef = new PortDef(
                            valueId,
                            argIndex,
                            p.getName(),
                            Port.Direction.INPUT,
                            valueType,
                            isAutoConnectable,
                            false);

                    result.add(portDef);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "INPUT of block definition unknown: "
                        + method.getClass().getSimpleName() + "."
                        + method.getName() + "() "
                        + p.getName(), e);
            }

            argIndex++;
        }
        return result;
    }

    public static PortDef outputDefFromReturnType(Method method) throws ReflectiveOperationException {
        Class<?> returnType = method.getReturnType();

        boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(returnType);

        try {
            var valueType = ValueTypeMappingUtils.fromType(returnType);
            var valueId = returnType.getSimpleName();

            var portDef = new PortDef(
                    valueId,
                    -1,
                    returnType.getSimpleName(),
                    Port.Direction.OUTPUT,
                    valueType,
                    isAutoConnectable,
                    false
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
