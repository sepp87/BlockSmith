package blocksmith.infra.blockloader;

import blocksmith.app.outbound.BlockExecLoader;
import blocksmith.exec.BlockExec;
import blocksmith.exec.SourceBlock;
import blocksmith.exec.SourceBlockSpec;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class SourceBlockExecLoader implements BlockExecLoader {

    private static final Logger LOGGER = Logger.getLogger(SourceBlockExecLoader.class.getName());

    private final SourceBlockScanner scanner;

    public SourceBlockExecLoader(SourceBlockScanner scanner) {
        this.scanner = scanner;
    }

    public Map<String, BlockExec> load() {

        var inspectors = scanner.classes();
        var result = new HashMap<String, BlockExec>();

        for (var inspector : inspectors) {

            var type = inspector.metadata().type();
            var source = sourceBlockFrom(inspector);

            if (result.containsKey(type)) {
                // TODO handle duplicate types
                continue;
            }

            result.put(type, source);

        }
        return result;
    }

    private static SourceBlockSpec sourceBlockFrom(SourceBlockInspector inspector) {
        var factory = inspector.factory();
        var injector = injectorFrom(inspector);
        return new SourceBlockSpec(factory, injector);

    }

    private static BiConsumer<SourceBlock, Object[]> injectorFrom(SourceBlockInspector clazz) {

        var inputMethod = clazz.inputMethod().orElse(null);
        if (inputMethod == null) {
            return null;
        }

        try {
            var inputHandle = MethodHandles.lookup().unreflect(inputMethod).asFixedArity();
            return (instance, inputs) -> {
                try {
                    var allArgs = new ArrayList<Object>();
                    allArgs.add(instance);
                    allArgs.addAll(Arrays.asList(inputs));
                    inputHandle.invokeWithArguments(allArgs);

                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            };

        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

    }
}
