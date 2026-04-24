package blocksmith.infra.blockloader;

import blocksmith.app.outbound.BlockDefLoader;
import blocksmith.domain.block.BlockDef;
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

    private final MethodBlockScanner scanner;

    public MethodBlockDefLoader(MethodBlockScanner scanner) {
        this.scanner = scanner;
    }

    public Collection<BlockDef> load() {
        var methods = scanner.methods();
        return blockDefsFromMethods(methods);
    }

    public static List<BlockDef> blockDefsFromMethods(Collection<Method> methods) {
        var result = new ArrayList<BlockDef>();

        for (Method method : methods) {
            try {
                var def = MethodBlockDefMapper.map(method);
                result.add(def);

            } catch (Exception ex) {
                LOGGER.log(
                        Level.SEVERE, 
                        "Block definition failed to load for method \"{0}.{1}()\". {2}", 
                        new Object[]{
                            method.getDeclaringClass().getSimpleName(), 
                            method.getName(), 
                            ex.getMessage()
                        }
                );
            }
        }

        return result;
    }

}
