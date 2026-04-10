package blocksmith.infra;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public final class AppPaths {

    private static final Logger LOGGER = Logger.getLogger(AppPaths.class.getName());

    private final Path root;

    private static final String BUILD_DIR = "build/";
    private static final String CONFIG_DIR = "config/";
    private static final String LIB_DIR = "lib/";

    private AppPaths() throws IOException {
        this.root = detectRoot(AppPaths.class, BUILD_DIR);
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

            // Development: .../<module>/target/classes → go two levels up
            if (filename.equals("classes")) {
                return location.getParent().getParent().getParent().resolve(fallbackSubdir);
            }

            // Otherwise assume packaged JAR (Production) → its containing folder 
            return location.getParent();

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to determine app root ({0}), using fallback: {1}", new Object[]{e.getMessage(), fallbackSubdir});
            return Paths.get(fallbackSubdir).toAbsolutePath();
        }
    }

    private Path resolve(String... parts) {
        // "" = neutral prefix, prevents IllegalArgumentException on empty input
        return root.resolve(Paths.get("", parts));
    }

    public Path getRoot() {
        return root;
    }

    public Path getConfigDir() {
        return resolve(CONFIG_DIR);
    }

    public Path getLibDir() {
        return resolve(LIB_DIR);
    }

    public static AppPaths create() throws IOException {
        var paths = new AppPaths();
        paths.ensureDir(paths.getConfigDir());
        paths.ensureDir(paths.getLibDir());
        return paths;
    }

    private void ensureDir(Path dir) throws IOException {
        if (Files.notExists(dir) || !Files.isDirectory(dir)) {
            LOGGER.log(Level.INFO, "Creating missing directory: {0}", dir);
            Files.createDirectories(dir);
        }
    }
}
