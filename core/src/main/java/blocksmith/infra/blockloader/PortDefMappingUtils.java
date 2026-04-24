package blocksmith.infra.blockloader;

import blocksmith.domain.value.AutoConnectable;
import blocksmith.domain.value.Port.Direction;
import blocksmith.domain.value.PortDef;
import blocksmith.domain.value.ValueType;
import blocksmith.infra.blockloader.annotations.Display;
import blocksmith.infra.blockloader.annotations.Value;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;

/**
 *
 * @author joost
 */
public class PortDefMappingUtils {

    private PortDefMappingUtils() {

    }

    public static PortDef fromParameter(
            Parameter parameter,
            int argIndex,
            Direction direction) {

        var isAggregated = parameter.isVarArgs();
        return PortDefMappingUtils.from(parameter, parameter.getType(), parameter.getParameterizedType(), parameter.getName(), argIndex, direction, isAggregated);
    }

    public static PortDef fromComponent(
            RecordComponent component,
            int argIndex,
            Direction direction) {

        return PortDefMappingUtils.from(component, component.getType(), component.getGenericType(), component.getName(), argIndex, direction, false);
    }

    public static PortDef fromReturnType(
            Method method,
            Direction direction,
            String valueName) {

        return PortDefMappingUtils.from(method.getReturnType(), method.getReturnType(), method.getGenericReturnType(), valueName, 0, direction, false);
    }

    private static PortDef from(
            AnnotatedElement annotated,
            Class<?> rawType,
            Type genericType,
            String valueName,
            int argIndex,
            Direction direction,
            boolean isAggregated) {

        boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(rawType);

        var value = annotated.getAnnotation(Value.class);
        var hasAnnotation = value != null;

        var valueType = ValueTypeMappingUtils.fromType(genericType);
        var valueId = hasAnnotation && !value.id().isEmpty() ? value.id() : valueName;

        var display = annotated.getAnnotation(Display.class) != null;
        var portDef = new PortDef(
                valueId,
                argIndex,
                valueName,
                direction,
                valueType,
                isAutoConnectable,
                display,
                isAggregated
        );
        return portDef;
    }
}
