package blocksmith.adapter.block;

import blocksmith.domain.block.BlockDef;
import blocksmith.domain.block.BlockTask;
import blocksmith.domain.block.Port;
import blocksmith.domain.block.PortDef;
import btscore.graph.block.BlockMetadata;
import btscore.graph.port.AutoConnectable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class MethodBlockDefLoader {

    private static final Logger LOGGER = Logger.getLogger(MethodBlockDefLoader.class.getName());

    private final Collection<Class<?>> methodLibraries;

    public MethodBlockDefLoader(Collection<Class<?>> methodLibraries) {
        this.methodLibraries = methodLibraries;
    }

    public Collection<BlockDef> load() {
        return blockDefsFromMethods(methodLibraries);
    }

    private static List<BlockDef> blockDefsFromMethods(Collection<Class<?>> classes) {
        var result = new ArrayList<BlockDef>();

        var methods = ReflectionUtils.getStaticMethodsFromClasses(classes);
        var eligble = ReflectionUtils.filterMethodsByAnnotation(methods, BlockMetadata.class);

        for (Method method : eligble) {
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
        var task = new BlockTask();

        return new BlockDef(metadata, inputs, List.of(output), task);
    }

    private static boolean isListOperator(Method method) {
        // If first input parameter is of type list, then this is a list operator block
        if (method.getParameters().length > 0 && List.class.isAssignableFrom(method.getParameters()[0].getType())) {
            return true;
        }
        return false;
    }

    private static boolean isListReturnType(Method method) {
        return false;
    }

    private static List<PortDef> inputDefsFromParameters(Method method) {
        var result = new ArrayList<PortDef>();

        for (Parameter p : method.getParameters()) {
            boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(p.getType());

            if (List.class.isAssignableFrom(p.getType())) {
                var portDef = new PortDef(Port.Direction.INPUT, Object.class);
                result.add(portDef);
//                blockModel.addInputPort("Object : List", Object.class, isAutoConnectable);

            } else {
                var portDef = new PortDef(Port.Direction.INPUT, p.getType());
                result.add(portDef);
//                blockModel.addInputPort(p.getName(), p.getType(), isAutoConnectable);
            }
        }
        return result;
    }

    private static PortDef outputDefFromReturnType(Method method) throws ReflectiveOperationException {
        Class<?> returnType = method.getReturnType();
        boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(returnType);

        if (returnType.equals(Number.class)) {
            return new PortDef(Port.Direction.OUTPUT, double.class);
//            blockModel.addOutputPort("double", double.class, isAutoConnectable);

        } else if (List.class.isAssignableFrom(returnType)) {

            Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType) { // if list TODO refactor since the return type is a ParameterizedType of type list
                ParameterizedType pt = (ParameterizedType) genericReturnType;
                Type typeArgument = pt.getActualTypeArguments()[0];
                if (typeArgument instanceof Class) {
                    Class<?> clazz = (Class<?>) typeArgument;
                    return new PortDef(Port.Direction.OUTPUT, double.class);
//                    blockModel.addOutputPort(clazz.getSimpleName(), clazz, isAutoConnectable);

                } else {
                    return new PortDef(Port.Direction.OUTPUT, Object.class);
//                    blockModel.isListReturnType = true;
//                    blockModel.addOutputPort(Object.class.getSimpleName(), Object.class, isAutoConnectable);
                }
            }

        } else {
            return new PortDef(Port.Direction.OUTPUT, returnType);
//            blockModel.addOutputPort(returnType.getSimpleName(), returnType, isAutoConnectable);

        }

        throw new ReflectiveOperationException("OUTPUT of block definition unknown");
    }

    private static PortDef portFromParameter(Method method) {
        return null;
    }

}
