package blocksmith.adapter.block;

import blocksmith.domain.block.Param;
import blocksmith.domain.block.Port;
import blocksmith.domain.block.PortDef;
import blocksmith.domain.block.ValueType.*;
import btscore.graph.port.AutoConnectable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

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
            boolean isParam = p.isAnnotationPresent(Param.class);

            if (isParam) {
                // is user input param, so skip

            } else if (List.class.isAssignableFrom(p.getType())) {
                if (p.getParameterizedType() instanceof ParameterizedType parameterizedType) {
                    var typeArgument = parameterizedType.getActualTypeArguments()[0];
                    if (typeArgument instanceof TypeVariable<?> typeVariable) {
                        var dataType = typeVariable.getBounds()[0].getClass();
                        var portDef = new PortDef(i, p.getName(), Port.Direction.INPUT, new ListType(new VarType(typeVariable.getName())), isAutoConnectable, true, true);
                        result.add(portDef);

                    } else {
                        var dataType = typeArgument.getClass(); // TODO can also be a list (edge case)
                        var portDef = new PortDef(i, p.getName(), Port.Direction.INPUT, new ListType(new SimpleType(dataType)), isAutoConnectable, true, false);
                        result.add(portDef);

                    }
                }

            } else {
                var portDef = new PortDef(i, p.getName(), Port.Direction.INPUT, new SimpleType(p.getType()), isAutoConnectable, false, false);
                result.add(portDef);
            }

            i++;
        }
        return result;
    }

    public static PortDef outputDefFromReturnType(Method method) throws ReflectiveOperationException {
        Class<?> returnType = method.getReturnType();

        boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(returnType);

        if (returnType == Integer.class || returnType == int.class) {
            return new PortDef(-1, "double", Port.Direction.OUTPUT, new SimpleType(Integer.class), isAutoConnectable, false, false);

        } else if (returnType == Number.class) {
            return new PortDef(-1, "double", Port.Direction.OUTPUT, new SimpleType(Double.class), isAutoConnectable, false, false);

        } else if (List.class.isAssignableFrom(returnType)) {

            Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType parameterizedType) {
                Type typeArgument = parameterizedType.getActualTypeArguments()[0];
                if (typeArgument instanceof TypeVariable<?> typeVariable) {
                    var dataType = typeVariable.getBounds()[0].getClass();
                    var portDef = new PortDef(-1, dataType.getSimpleName(), Port.Direction.OUTPUT, new ListType(new VarType(typeVariable.getName())), isAutoConnectable, true, true);
                    return portDef;

                } else {
                    var dataType = typeArgument.getClass(); // TODO could also be of type list
                    var portDef = new PortDef(-1, dataType.getSimpleName(), Port.Direction.OUTPUT, new ListType(new SimpleType(dataType)), isAutoConnectable, true, false);
                    return portDef;
                }
            }

        } else {
            return new PortDef(-1, returnType.getSimpleName(), Port.Direction.OUTPUT, new SimpleType(returnType), isAutoConnectable, false, false);

        }

        throw new ReflectiveOperationException("OUTPUT of block definition unknown");
    }

    private static PortDef portFromParameter(Method method) {
        return null;
    }
}
