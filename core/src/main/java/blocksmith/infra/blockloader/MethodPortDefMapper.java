package blocksmith.infra.blockloader;

import blocksmith.domain.value.Port;
import blocksmith.domain.value.PortDef;
import blocksmith.infra.blockloader.annotations.Display;
import blocksmith.domain.value.AutoConnectable;
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

    private static final String DEFAULT_OUPUT_VALUE_ID = "value";

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

                    var display = p.getAnnotation(Display.class) != null;
                    var portDef = new PortDef(
                            valueId,
                            argIndex,
                            p.getName(),
                            Port.Direction.INPUT,
                            valueType,
                            isAutoConnectable,
                            display);

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
            Value value = method.getAnnotatedReturnType().getAnnotation(Value.class);
            boolean hasAnnotation = value != null;

            var valueType = ValueTypeMappingUtils.fromType(method.getGenericReturnType());
            var valueId = hasAnnotation && !value.id().isEmpty() ? value.id() : DEFAULT_OUPUT_VALUE_ID;

            var portDef = new PortDef(
                    valueId,
                    -1,
                    valueId,
                    Port.Direction.OUTPUT,
                    valueType,
                    isAutoConnectable,
                    false
            );

            return portDef;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "OUTPUT of block definition unknown. " + e.getMessage());
        }

    }

}
