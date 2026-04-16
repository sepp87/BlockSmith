package blocksmith.infra.blockloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public final class ClassScannerUtils {

    private static final Logger LOGGER = Logger.getLogger(ClassScanner.class.getName());

    private ClassScannerUtils() {
    }

    /**
     * Scans the given package (and sub-packages) within the context classloader
     * — own project + any JARs already on the classpath.
     */
    public static List<Class<?>> findOnClasspath(String packageName) {
        String resourcePath = packageName.replace('.', '/');
        List<Class<?>> result = new ArrayList<>();

        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(resourcePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if ("file".equals(resource.getProtocol())) {
                    scanDirectory(new File(resource.getFile()), packageName, result);
                } else if ("jar".equals(resource.getProtocol())) {
                    String jarPath = resource.getPath();
                    jarPath = jarPath.substring(5, jarPath.indexOf('!'));
                    loadFromJar(new File(jarPath), resourcePath, Thread.currentThread().getContextClassLoader(), result);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        return result;
    }

    /**
     * Scans plugin JARs from disk, using each JAR's manifest 'package'
     * attribute to filter classes.
     */
    public static List<Class<?>> findInJars(File... jars) {
        List<Class<?>> result = new ArrayList<>();
        for (File jar : jars) {
            try {
                String packageName = getManifestPackage(jar);
                if (packageName == null) {
                    continue;
                }
                URLClassLoader loader = new URLClassLoader(new URL[]{jar.toURI().toURL()});
                loadFromJar(jar, packageName.replace('.', '/'), loader, result);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        }
        return result;
    }

    private static void scanDirectory(File dir, String packageName, List<Class<?>> result) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), result);
            } else if (file.getName().endsWith(".class") && !file.getName().contains("$")) {
                load(packageName + "." + file.getName().replace(".class", ""),
                        Thread.currentThread().getContextClassLoader(), result);
            }
        }
    }

    private static void loadFromJar(File jar, String resourcePath, ClassLoader loader, List<Class<?>> result) throws IOException {
        try (JarFile jarFile = new JarFile(jar)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(resourcePath) && name.endsWith(".class") && !name.contains("$")) {
                    load(name.replace('/', '.').replace(".class", ""), loader, result);
                }
            }
        }
    }

    private static String getManifestPackage(File jar) throws IOException {
        try (JarFile jarFile = new JarFile(jar)) {
            return jarFile.getManifest().getMainAttributes().getValue("package");
        }
    }

    private static void load(String className, ClassLoader loader, List<Class<?>> result) {
        try {
            result.add(loader.loadClass(className));
        } catch (ClassNotFoundException | LinkageError e) {
            LOGGER.log(Level.WARNING, "Could not load class: {0}", className);
        }
    }
}
