package blocksmith.adapter.block;

import blocksmith.app.ports.BlockDefLoader;
import blocksmith.domain.block.BlockDef;
import btscore.graph.block.BlockMetadata;
import java.lang.reflect.Method;
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
                var def = MethodBlockDefMapper.blockDefFromMethod(method);
                result.add(def);

            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Block definition failed to load for method: " + method.getName(), ex);
            }
        }

        return result;
    }




}
