package blocksmith.infra.blockloader;

import blocksmith.domain.value.ParamDef;
import blocksmith.domain.value.ParamInput;
import blocksmith.domain.value.ParamInput.NumericType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import blocksmith.infra.blockloader.annotations.Value;

/**
 *
 * @author joost
 */
public class MethodParamDefMapper {

    public static List<ParamDef> paramDefsFromParameters(Method method) throws Exception {
        var result = new ArrayList<ParamDef>();
        int argIndex = 0;
        for (Parameter parameter : method.getParameters()) {
            var value = parameter.getAnnotation(Value.class);
            var isParam = value != null && value.source() != Value.Source.PORT;

            // TODO throw exception for unsupported param data types
            if (isParam) {

                var valueName = parameter.getName();
                var valueId = !value.id().isEmpty() ? value.id() : valueName;
                var dataType = parameter.getType();
                var valueType = ValueTypeMappingUtils.fromMethodParameter(parameter);
                var input = paramInputFrom(value, dataType, method);

                var paramDef = new ParamDef(valueId, argIndex, valueName, valueType, input);
                result.add(paramDef);
            }
            argIndex++;
        }
        return result;
    }

    private static ParamInput paramInputFrom(Value param, Class<?> dataType, Method method) throws Exception {

        if (param.input() == ParamInput.Choice.class && dataType == String.class) {
            var returnType = method.getReturnType();
            var choices = choicesFrom(returnType);
            return new ParamInput.Choice(choices);

        } else if (param.input() == ParamInput.Range.class) {
            var returnType = method.getReturnType();
            var numericType = numericTypeFrom(returnType);
            return new ParamInput.Range(numericType);

        }
        return param.input().getDeclaredConstructor().newInstance();
    }

    private static List<String> choicesFrom(Class<?> returnType) {
        var result = new ArrayList<String>();
        if (returnType.isEnum()) {
            var enums = (Enum<?>[]) returnType.getEnumConstants();

            for (var e : enums) {
                result.add(e.name());
            }
        }
        return result;
    }

    private static NumericType numericTypeFrom(Class<?> returnType) {

        if (returnType == Integer.class || returnType == int.class) {
            return NumericType.INT;

        } else if (returnType == Double.class || returnType == double.class) {
            return NumericType.DOUBLE;
        }
        throw new IllegalArgumentException("Return type not supported for param input type range: " + returnType);
    }
}
