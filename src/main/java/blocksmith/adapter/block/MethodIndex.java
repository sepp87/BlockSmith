package blocksmith.adapter.block;

import blocksmith.adapter.AppPaths;
import btscore.graph.block.BlockMetadata;
import btscore.utils.JarClassLoaderUtils;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joost
 */
public class MethodIndex {

    private final AppPaths paths;
    private final List<Method> methods;
    

    public MethodIndex(Collection<Class<?>> classes) throws IOException {
        this.paths = new AppPaths();
        this.methods = List.copyOf(loadEligbleMethods(classes));
    }


    public Collection<Method> methods() {
        return methods;
    }

    private Collection<Method> loadEligbleMethods(Collection<Class<?>> classes) {
        var methods = MethodLoaderUtils.getStaticMethodsFromClasses(classes);
        var eligble = MethodLoaderUtils.filterMethodsByAnnotation(methods, BlockMetadata.class);
        return eligble;
    }

    
}
