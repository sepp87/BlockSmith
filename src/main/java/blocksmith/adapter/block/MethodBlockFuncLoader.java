package blocksmith.adapter.block;

import blocksmith.exec.BlockFunc;
import blocksmith.app.ports.BlockFuncLoader;
import btscore.graph.block.BlockMetadata;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author joost
 */
public class MethodBlockFuncLoader implements BlockFuncLoader {

    private final Collection<Method> methods;

    public MethodBlockFuncLoader(Collection<Method> methods) {
        this.methods = methods;
    }

    @Override
    public Map<String, BlockFunc> load() {
        
        var result = new HashMap<String, BlockFunc>();
        
        for(var method : methods) {
            
            var type = method.getAnnotation(BlockMetadata.class).type();
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
            MethodHandle handle = MethodHandles.lookup().unreflect(method);

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
