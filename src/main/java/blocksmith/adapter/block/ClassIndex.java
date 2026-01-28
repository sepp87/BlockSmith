
package blocksmith.adapter.block;

import blocksmith.adapter.AppPaths;
import btscore.utils.JarClassLoaderUtils;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public class ClassIndex {
    
    private final AppPaths paths;
    private final List<Class<?>> classes;
    
    public ClassIndex(AppPaths paths) {
        this.paths = paths;
        this.classes = List.copyOf(loadClasses());
    }
    
    public Collection<Class<?>> classes () {
        return classes;
    }
    
    private List<Class<?>> loadClasses() {
        var result = new ArrayList<>(internalClasses());
        result.addAll(loadExternalClasses());
        return result;
    }
    
    private List<Class<?>> internalClasses() {
        return List.of(
                btslib.method.DateMethods.class,
                btslib.method.FileMethods.class,
                btslib.method.JsonMethods.class,
                btslib.method.ListMethods.class,
                btslib.method.MathMethods.class,
                btslib.method.ObjectMethods.class,
                btslib.method.SpreadsheetMethods.class,
                btslib.method.StringMethods.class
        );
    }

    private List<Class<?>> loadExternalClasses() {
        var jars = paths.getJarFiles().stream().map(Path::toFile).toArray(File[]::new);
        return JarClassLoaderUtils.getClassesFromLibraries(jars);
    }
}
