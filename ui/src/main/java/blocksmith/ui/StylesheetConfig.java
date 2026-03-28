package blocksmith.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.scene.Scene;

/**
 *
 * @author joost
 */
public class StylesheetConfig {

    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(blocksmith.app.Config.class);

    private static final String PREF_STYLESHEET = "stylesheet";

    private static final String DEFAULT_STYLE = "Dark";

    public static final Map<String, String> STYLESHEETS = new HashMap<String, String>() {
        {
            put("Dark", "css/dark_mode.css");
            put("Light", "css/flat_white.css");
            put("Singer", "css/flat_singer.css");
        }
    };

    public static String getStylesheet() {
        String styleOrPath = PREFERENCES.get(PREF_STYLESHEET, STYLESHEETS.get(DEFAULT_STYLE)); // Predefined style or path to user defined CSS
        if (STYLESHEETS.containsValue(styleOrPath)) {
            return styleOrPath;
        }
        File userDefinedStyle = new File(styleOrPath);
        if (userDefinedStyle.exists()) {
            return styleOrPath;
        }
        return STYLESHEETS.get(DEFAULT_STYLE);
    }

    public static void setStylesheet(Scene scene, String style) {
        setStylesheetToPreferences(style);
        setStylesheetToScene(scene);
    }

    private static void setStylesheetToPreferences(String style) {
        // save app provided stylesheet to preferences
        if (STYLESHEETS.containsKey(style)) {
            PREFERENCES.put(PREF_STYLESHEET, STYLESHEETS.get(style));
        }

        // save user provided stylesheet to preferences
        File stylesheet = new File(style);
        if (stylesheet.exists()) { // TODO check if file is really a stylesheet
            PREFERENCES.put(PREF_STYLESHEET, style);
        }
    }

    public static void setStylesheetToScene(Scene scene) {
        // Load the CSS from classpath using ClassLoader
        String stylesheetPath = getStylesheet(); // Adjust based on your structure
        URL resourceUrl = blocksmith.app.Config.get().getClass().getClassLoader().getResource(stylesheetPath);

        if (resourceUrl != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(resourceUrl.toExternalForm());
            System.out.println("CSS Loaded: " + resourceUrl.toExternalForm());
        } else {
            System.err.println("Stylesheet not found: " + stylesheetPath);
            return;
        }

        // Enable file watching only if running in IDE (not in JAR)
        Path filePath = Paths.get("src/main/resources/", stylesheetPath); // Path in IDE
        if (Files.exists(filePath)) {
            watchForCssChanges(scene, filePath);
        }
    }

    private static void watchForCssChanges(Scene scene, Path path) {
        new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.context().toString().equals(path.getFileName().toString())) {
                            Platform.runLater(() -> {
                                scene.getStylesheets().clear();
                                scene.getStylesheets().add(path.toUri().toString());
                                System.out.println("CSS Reloaded!");
                            });
                        }
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
