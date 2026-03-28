
package blocksmith.infra.blockloader;

import blocksmith.infra.AppPaths;
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
                blocksmith.lib.BooleanMethods.class,
                blocksmith.lib.DateMethods.class,
                blocksmith.lib.FileMethods.class,
                blocksmith.lib.JsonMethods.class,
                blocksmith.lib.ListMethods.class,
                blocksmith.lib.MathMethods.class,
                blocksmith.lib.NumberMethods.class,
                blocksmith.lib.ObjectMethods.class,
                blocksmith.lib.SpreadsheetMethods.class,
                blocksmith.lib.StringMethods.class
        );
    }

    private List<Class<?>> loadExternalClasses() {
        var jars = paths.getJarFiles().stream().map(Path::toFile).toArray(File[]::new);
        return JarClassLoaderUtils.getClassesFromLibraries(jars);
    }
}
