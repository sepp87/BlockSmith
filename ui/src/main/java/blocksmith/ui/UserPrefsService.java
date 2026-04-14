package blocksmith.ui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.prefs.Preferences;

/**
 *
 * @author joost
 */
public class UserPrefsService { // move to infra in the future e.g. if headless mode also needs user prefs

    private static final Preferences PREFS = Preferences.userNodeForPackage(UserPrefsService.class);
    private static final String PREF_LAST_OPENED_DIR = "lastOpenedDir";
    private static final String PREF_SHOW_HELP_ON_START = "showHelp";
    private static final String PREF_INITIAL_DOCUMENT = "initialDocument";

    public Optional<Path> getLastOpenedDir() {
        String raw = PREFS.get(PREF_LAST_OPENED_DIR, null);
        if (raw == null) {
            return Optional.empty();
        }
        var path = Path.of(raw);
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        return Optional.of(path);
    }

    public void setLastOpenedDir(Path path) {
        if (path == null || Files.exists(path)) {
            return;
        }
        if (Files.isDirectory(path)) {
            PREFS.put(PREF_LAST_OPENED_DIR, path.toString());
        } else {
            PREFS.put(PREF_LAST_OPENED_DIR, path.getParent().toString());
        }
    }

    public boolean showHelpOnStart() {
        return PREFS.getBoolean(PREF_SHOW_HELP_ON_START, true);
    }

    public void setShowHelpOnStart(boolean show) {
        PREFS.putBoolean(PREF_SHOW_HELP_ON_START, show);
    }

    public Optional<Path> getInitialDocument() {
        String raw = PREFS.get(PREF_INITIAL_DOCUMENT, null);
        if (raw == null) {
            return Optional.empty();
        }
        var path = Path.of(raw);
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        return Optional.of(path);
    }

    public void setInitialDocument(Path path) {
        if (path == null || !Files.exists(path)) {
            return;
        }
        if (Files.isRegularFile(path)) {
            PREFS.put(PREF_INITIAL_DOCUMENT, path.toString());
        }
    }
    
    public static UserPrefsService forDev(Path path) {
        var service = new UserPrefsService();
        service.setInitialDocument(path);
        return service;
    }
    
    public static UserPrefsService forProd() {
        return new UserPrefsService();
    }
}
