package blocksmith.adapter.block;

import blocksmith.app.ports.BlockDefLoader;
import blocksmith.domain.block.BlockDef;
import blocksmith.domain.block.BlockTask;
import blocksmith.domain.block.Port;
import blocksmith.domain.block.PortDef;
import btscore.graph.block.BlockMetadata;
import btscore.graph.port.AutoConnectable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class MethodBlockDefLoader implements BlockDefLoader {

    private static final Logger LOGGER = Logger.getLogger(MethodBlockDefLoader.class.getName());

//    private final Collection<Class<?>> methodLibraries;
    private final Collection<Method> methods;

    public MethodBlockDefLoader(Collection<Method> methods) {
        this.methods = methods;
    }

//    public MethodBlockDefLoader(Collection<Class<?>> methodLibraries) {
//        this.methodLibraries = methodLibraries;
//    }
    public Collection<BlockDef> load() {
        return blockDefsFromMethods(methods);
    }

    public static List<BlockDef> blockDefsFromMethods(Collection<Method> methods) {
        var result = new ArrayList<BlockDef>();

        for (Method method : methods) {
            try {
                var definition = blockDefFromMethod(method);
                result.add(definition);

            } catch (ReflectiveOperationException ex) {
                LOGGER.log(Level.SEVERE, "Block definition failed to load for annotated method: " + method.getName(), ex);
            }
        }

        return result;
    }

    private static BlockDef blockDefFromMethod(Method method) throws ReflectiveOperationException {

        var metadata = method.getAnnotation(BlockMetadata.class);
        var inputs = inputDefsFromParameters(method);
        var output = outputDefFromReturnType(method);

        return new BlockDef(metadata, inputs, List.of(output));
    }

    // TODO move somewhere else
    private static boolean isListOperator(Method method) {
        // If first input parameter is of type list, then this is a list operator block
        if (method.getParameters().length > 0 && List.class.isAssignableFrom(method.getParameters()[0].getType())) {
            return true;
        }
        return false;
    }

    private static List<PortDef> inputDefsFromParameters(Method method) {
        var result = new ArrayList<PortDef>();

        for (Parameter p : method.getParameters()) {

            if (List.class.isAssignableFrom(p.getType())) {
                if (p.getParameterizedType() instanceof ParameterizedType parameterizedType) {
                    var typeArgument = parameterizedType.getActualTypeArguments()[0];
                    if (typeArgument instanceof TypeVariable<?> typeVariable) {
                        var dataType = typeVariable.getBounds()[0].getClass();
                        var portDef = new PortDef(p.getName(), Port.Direction.INPUT, dataType, true);
                        result.add(portDef);

                    } else {
                        var dataType = typeArgument.getClass();
                        var portDef = new PortDef(p.getName(), Port.Direction.INPUT, dataType, false);
                        result.add(portDef);

                    }
                }

            } else {
                var portDef = new PortDef(p.getName(), Port.Direction.INPUT, p.getType(), false);
                result.add(portDef);
            }
        }
        return result;
    }

    private static PortDef outputDefFromReturnType(Method method) throws ReflectiveOperationException {
        Class<?> returnType = method.getReturnType();

        if (returnType.equals(Number.class)) {
            return new PortDef("double", Port.Direction.OUTPUT, double.class, false);

        } else if (List.class.isAssignableFrom(returnType)) {

            Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType parameterizedType) {
                Type typeArgument = parameterizedType.getActualTypeArguments()[0];
                if (typeArgument instanceof TypeVariable<?> typeVariable) {
                    var dataType = typeVariable.getBounds()[0].getClass();
                    var portDef = new PortDef(dataType.getSimpleName(), Port.Direction.OUTPUT, dataType, true);
                    return portDef;

                } else {
                    var dataType = typeArgument.getClass();
                    var portDef = new PortDef(dataType.getSimpleName(), Port.Direction.OUTPUT, dataType, false);
                    return portDef;
                }
            }

        } else {
            return new PortDef(returnType.getSimpleName(), Port.Direction.OUTPUT, returnType, false);

        }

        throw new ReflectiveOperationException("OUTPUT of block definition unknown");
    }

    private static PortDef portFromParameter(Method method) {
        return null;
    }

}
