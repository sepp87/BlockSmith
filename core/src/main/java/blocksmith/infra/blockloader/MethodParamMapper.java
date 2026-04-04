package blocksmith.infra.blockloader;

import blocksmith.domain.value.ParamDef;
import blocksmith.domain.value.ParamInput;
import blocksmith.domain.value.ParamInput.NumericType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import blocksmith.infra.blockloader.annotations.Value;
import java.time.LocalDate;
import java.util.Optional;

/**
 *
 * @author joost
 */
public class MethodParamMapper {

    public static List<ParamDef> map(Method method) throws Exception {
        var result = new ArrayList<ParamDef>();
        int argIndex = 0;
        for (Parameter parameter : method.getParameters()) {
            var value = parameter.getAnnotation(Value.class);
            var isParam = value != null && value.source() != Value.Source.PORT;
            var isString = parameter.getType() == String.class;

            // TODO throw exception for unsupported param data types
            if (isParam) {

                var valueName = parameter.getName();
                var valueId = !value.id().isEmpty() ? value.id() : valueName;
                var valueType = ValueTypeMappingUtils.fromMethodParameter(parameter);
                var input = paramInputFrom(value, method);

                var paramDef = new ParamDef(valueId, argIndex, valueName, valueType, input);
                result.add(paramDef);
            }
            argIndex++;
        }
        return result;
    }

    private static ParamInput paramInputFrom(Value param, Method method) throws Exception {
        var returnType = method.getReturnType();

        if (param.input() == ParamInput.Unspecified.class) {
            return inferInputFrom(returnType);

        } else if (param.input() == ParamInput.Choice.class) {
            return choiceInputFrom(returnType);

        } else if (param.input() == ParamInput.Range.class) {
            return rangeInputFrom(returnType)
                    .orElseThrow(() -> new IllegalArgumentException("Return type not supported for param input type range: " + returnType));

        }
        return param.input().getDeclaredConstructor().newInstance();
    }

    private static ParamInput inferInputFrom(Class<?> returnType) {
        if (returnType == Boolean.class || returnType == boolean.class) {
            return new ParamInput.Boolean();

        } else if (returnType == LocalDate.class) {
            return new ParamInput.Date();

        } else if (returnType.isEnum()) {
            return choiceInputFrom(returnType);

        }
        return rangeInputFrom(returnType)
                .orElse(new ParamInput.Text()); // fallback
    }

    private static ParamInput.Choice choiceInputFrom(Class<?> returnType) {
        var choices = new ArrayList<String>();
        if (returnType.isEnum()) {
            var enums = (Enum<?>[]) returnType.getEnumConstants();

            for (var e : enums) {
                choices.add(e.name());
            }
        }
        return new ParamInput.Choice(choices);
    }

    private static Optional<ParamInput> rangeInputFrom(Class<?> returnType) {

        if (returnType == Integer.class || returnType == int.class) {
            return Optional.of(ParamInput.Range.ofInteger());

        } else if (returnType == Double.class || returnType == double.class) {
            return Optional.of(ParamInput.Range.ofDecimal());
        }
        return Optional.empty();
    }
}
