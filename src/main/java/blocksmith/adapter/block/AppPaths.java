package blocksmith.adapter.block;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author joost
 */
public final class AppPaths {

    private static final Logger LOGGER = Logger.getLogger(AppPaths.class.getName());

    private final Path root;
    
    private static final String BUILD_DIRECTORY = "build";
    private static final String CONFIG_DIRECTORY = "config";
    private static final String LIB_DIRECTORY = "lib";

    public AppPaths() throws IOException {
        this.root = detectRoot(AppPaths.class, BUILD_DIRECTORY);
        ensureDir(getConfigDirectory());
        ensureDir(getLibDirectory());
    }

    /**
     * Detects the application root dynamically — depending if running from IDE
     * (target/classes) or packaged JAR.
     */
    private Path detectRoot(Class<?> anchor, String fallbackSubdir) {
        try {
            var uri = anchor.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI();
            Path location = Paths.get(uri);
            String filename = location.getFileName().toString();

            // Development: .../target/classes → go two levels up
            if (filename.equals("classes")) {
                return location.getParent().getParent().resolve(fallbackSubdir);
            }

            // Otherwise assume packaged JAR (Production) → its containing folder 
            return location.getParent();

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to determine app root ({0}), using fallback: {1}", new Object[]{e.getMessage(), fallbackSubdir});
            return Paths.get(fallbackSubdir).toAbsolutePath();
        }
    }

    private void ensureDir(Path dir) throws IOException {
        if (Files.notExists(dir) || !Files.isDirectory(dir)) {
            LOGGER.log(Level.INFO, "Creating missing directory: {0}", dir);
            Files.createDirectories(dir);
        }
    }

    private Path resolve(String... parts) {
        // "" = neutral prefix, prevents IllegalArgumentException on empty input
        return root.resolve(Paths.get("", parts));
    }

    public Path getRoot() {
        return root;
    }

    public Path getConfigDirectory() {
        return resolve(CONFIG_DIRECTORY);
    }

    public Path getLibDirectory() {
        return resolve(LIB_DIRECTORY);
    }

    public List<Path> getJarFiles() {

        Path dir = getLibDirectory();
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
