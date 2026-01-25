package blocksmith.adapter.block;

import btscore.Config;
import btscore.utils.FileUtils;
import btscore.utils.JarClassLoaderUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joost
 */
public class MethodClassProvider {

    private final AppPaths paths;
    private final List<Class<?>> externalLibraries;

    public MethodClassProvider() throws IOException {
        this.paths = new AppPaths();
        
        externalLibraries = loadExternalLibraries();
    }

    public Collection<Class<?>> externalMethodLibraries() {
        return externalLibraries;
    }
    
    public Collection<Class<?>> externalClassLibraries() {
        return externalLibraries;
    }

    public Collection<Class<?>> internalMethodLibraries() {
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

    private List<Class<?>> loadExternalLibraries() {
        var jars = paths.getJarFiles().stream().map(Path::toFile).toArray(File[]::new);
        return JarClassLoaderUtils.getClassesFromLibraries(jars);
    }
}
