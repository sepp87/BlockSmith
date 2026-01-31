package blocksmith.adapter.block;

import blocksmith.app.ports.BlockDefLoader;
import blocksmith.domain.block.BlockDef;
import blocksmith.domain.block.ParamDef;
import blocksmith.domain.block.Port;
import blocksmith.domain.block.PortDef;
import btscore.graph.block.BlockMetadata;
import btscore.graph.port.AutoConnectable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import blocksmith.domain.block.Param;
import blocksmith.domain.block.ParamInput;

/**
 *
 * @author joost
 */
public class MethodBlockDefLoader implements BlockDefLoader {

    private static final Logger LOGGER = Logger.getLogger(MethodBlockDefLoader.class.getName());

    private final Collection<Method> methods;

    public MethodBlockDefLoader(Collection<Method> methods) {
        this.methods = methods;
    }

    public Collection<BlockDef> load() {
        return blockDefsFromMethods(methods);
    }

    public static List<BlockDef> blockDefsFromMethods(Collection<Method> methods) {
        var result = new ArrayList<BlockDef>();

        for (Method method : methods) {
            try {
                var definition = blockDefFromMethod(method);
                result.add(definition);

            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Block definition failed to load for annotated method: " + method.getName(), ex);
            }
        }

        return result;
    }

    private static BlockDef blockDefFromMethod(Method method) throws Exception {

        var metadata = method.getAnnotation(BlockMetadata.class);
        var inputs = inputDefsFromParameters(method);
        var output = outputDefFromReturnType(method);
        var params = paramDefsFromParameters(method);
        var isListOperator = isListOperator(method);

        return new BlockDef(metadata, inputs, List.of(output), params, isListOperator);
    }

    // TODO move somewhere else
    private static boolean isListOperator(Method method) {
        // If first input parameter is of type list, then this is a list operator block
        if (method.getParameters().length > 0 && List.class.isAssignableFrom(method.getParameters()[0].getType())) {
            return true;
        }
        return false;
    }

    private static List<ParamDef> paramDefsFromParameters(Method method) throws Exception {
        var result = new ArrayList<ParamDef>();
        int i = 0;
        for (Parameter p : method.getParameters()) {
            var param = p.getAnnotation(Param.class);
            var isParam = param != null;

            // TODO throw exception for unsupported param data types
            if (isParam) {

                var name = p.getName();
                var dataType = p.getType();
                var spec = param.input().getDeclaredConstructor().newInstance();

                var paramDef = new ParamDef(i, name, dataType, spec);
                result.add(paramDef);
            }
            i++;
        }
        return result;
    }


    private static List<PortDef> inputDefsFromParameters(Method method) {
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
                        var portDef = new PortDef(i, p.getName(), Port.Direction.INPUT, dataType, isAutoConnectable, true, true);
                        result.add(portDef);

                    } else {
                        var dataType = typeArgument.getClass();
                        var portDef = new PortDef(i, p.getName(), Port.Direction.INPUT, dataType, isAutoConnectable, true, false);
                        result.add(portDef);

                    }
                }

            } else {
                var portDef = new PortDef(i, p.getName(), Port.Direction.INPUT, p.getType(), isAutoConnectable, false, false);
                result.add(portDef);
            }

            i++;
        }
        return result;
    }

    private static PortDef outputDefFromReturnType(Method method) throws ReflectiveOperationException {
        Class<?> returnType = method.getReturnType();

        boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(returnType);

        if (returnType.equals(Number.class)) {
            return new PortDef(-1, "double", Port.Direction.OUTPUT, double.class, isAutoConnectable, false, false);

        } else if (List.class.isAssignableFrom(returnType)) {

            Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType parameterizedType) {
                Type typeArgument = parameterizedType.getActualTypeArguments()[0];
                if (typeArgument instanceof TypeVariable<?> typeVariable) {
                    var dataType = typeVariable.getBounds()[0].getClass();
                    var portDef = new PortDef(-1, dataType.getSimpleName(), Port.Direction.OUTPUT, dataType, isAutoConnectable, true, true);
                    return portDef;

                } else {
                    var dataType = typeArgument.getClass();
                    var portDef = new PortDef(-1, dataType.getSimpleName(), Port.Direction.OUTPUT, dataType, isAutoConnectable, true, false);
                    return portDef;
                }
            }

        } else {
            return new PortDef(-1, returnType.getSimpleName(), Port.Direction.OUTPUT, returnType, isAutoConnectable, false, false);

        }

        throw new ReflectiveOperationException("OUTPUT of block definition unknown");
    }

    private static PortDef portFromParameter(Method method) {
        return null;
    }

}
