package blocksmith.infra.blockloader;

import blocksmith.exec.block.BlockFunc;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import blocksmith.infra.blockloader.annotations.Block;
import blocksmith.app.outbound.BlockExecLoader;
import blocksmith.exec.block.BlockExec;

/**
 *
 * @author joost
 */
public class MethodBlockFuncLoader implements BlockExecLoader {

    private final MethodBlockScanner scanner;

    public MethodBlockFuncLoader(MethodBlockScanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public Map<String, BlockExec> load() {
        
        var methods = scanner.methods();
        var result = new HashMap<String, BlockExec>();
        
        for(var method : methods) {
            
            var type = method.getAnnotation(Block.class).type();
            var func = blockFuncFrom(method);
            
            if(result.containsKey(type)) {
                // TODO handle duplicate types
                continue;
            }
            
            result.put(type, func);
            
        }
        return result;
    }

    private static BlockFunc blockFuncFrom(Method method) {
        try {
            MethodHandle handle = MethodHandles.lookup().unreflect(method).asFixedArity();

            return inputs -> {
                try {
                    return handle.invokeWithArguments(inputs); // method handles can throw Throwables, but BlockFunc requires Exception
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            };

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
