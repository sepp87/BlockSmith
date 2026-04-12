package blocksmith.infra.blockloader;

import blocksmith.app.outbound.BlockScanner;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import blocksmith.infra.blockloader.annotations.Block;

/**
 *
 * @author joost
 */
public class MethodBlockScanner implements BlockScanner {

    private final ClassScanner classScanner;

    private List<Method> methods;

    public MethodBlockScanner(ClassScanner classScanner) throws IOException {
        this.classScanner = classScanner;
        rescan();
    }

    public void rescan() {
        var classes = classScanner.classes();
        this.methods = List.copyOf(loadEligible(classes));
    }

    public Collection<Method> methods() {
        return methods;
    }

    private Collection<Method> loadEligible(Collection<Class<?>> classes) {
        var methods = MethodLoaderUtils.getStaticMethodsFromClasses(classes);
        var eligible = MethodLoaderUtils.filterMethodsByAnnotation(methods, Block.class);
        return eligible;
    }

}
