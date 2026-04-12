package blocksmith.infra.blockloader;

import blocksmith.exec.SourceBlock;
import blocksmith.infra.blockloader.annotations.Block;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class SourceBlockInspector {

    private final Class<? extends SourceBlock> clazz;
    private final Map<String, Method> methods;

    public SourceBlockInspector(Class<?> clazz) {
        this.clazz = sourceBlock(clazz);
        this.methods = MethodLoaderUtils.methodsByName(clazz);
        validate();
    }

    @SuppressWarnings("unchecked")
    private Class<? extends SourceBlock> sourceBlock(Class<?> clazz) {
        if (!SourceBlock.class.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Class " + clazz.getSimpleName() + " is NOT valid source block.");
        }
        return (Class<? extends SourceBlock>) clazz;
    }

    private void validate() {
        validateModifiersOf(methods.get(SourceBlock.INPUT_METHOD));
        validateModifiersOf(methods.get(SourceBlock.OUTPUT_METHOD));
    }

    private static void validateModifiersOf(Method method) {
        if (method == null) {
            return;
        }
        if (!MethodLoaderUtils.isInstance(method)) {
            throw new IllegalStateException("Method " + method.getName() + " should NOT be declared static.");
        }
        if (!MethodLoaderUtils.isPublic(method)) {
            throw new IllegalStateException("Method " + method.getName() + " is NOT declared public.");

        }
    }

    public Block metadata() {
        return clazz.getAnnotation(Block.class);
    }

    public Optional<Method> inputMethod() {
        return Optional.ofNullable(methods.get(SourceBlock.INPUT_METHOD));
    }

    public Optional<Method> outputMethod() {
        return Optional.ofNullable(methods.get(SourceBlock.OUTPUT_METHOD));
    }

    public Supplier<SourceBlock> factory() {
        return () -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(SourceBlockInspector.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new RuntimeException("Could NOT instantiate block executable for: " + metadata().type());
        };

    }
}
