package blocksmith.infra.blockloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author joostmeulenkamp
 */
public class ClassScanner {

    private static final Logger LOGGER = Logger.getLogger(ClassScanner.class.getName());
    
    private final Path libDirectory;
    private List<Class<?>> classes;

    public ClassScanner(Path libDirectory) {
        this.libDirectory = libDirectory;
        rescan();
    }
    
    public void rescan() {
        this.classes = List.copyOf(loadClasses());
    } 

    public Collection<Class<?>> classes() {
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
                blocksmith.lib.HelloWorldMethods.class,
                blocksmith.lib.JsonMethods.class,
                blocksmith.lib.ListMethods.class,
                blocksmith.lib.LocaleMethods.class,
                blocksmith.lib.MathMethods.class,
                blocksmith.lib.NumberMethods.class,
                blocksmith.lib.ObjectMethods.class,
                blocksmith.lib.SpreadsheetMethods.class,
                blocksmith.lib.StringMethods.class
        );
    }

    private List<Class<?>> loadExternalClasses() {
        var jars = getJarFiles(libDirectory).stream().map(Path::toFile).toArray(File[]::new);
        return JarClassLoaderUtils.getClassesFromLibraries(jars);
    }

    private List<Path> getJarFiles(Path dir) {
        try (Stream<Path> dirContent = Files.list(dir)) {
            return dirContent
                    .filter(path -> (Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(".jar")))
                    .toList();

        } catch (IOException ex) {
            LOGGER.log(Level.INFO, "Error reading directory {0}: {1}", new Object[]{dir.getFileName(), ex.getMessage()});
        }
        return Collections.emptyList();
    }
}
