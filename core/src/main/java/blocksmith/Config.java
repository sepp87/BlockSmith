package blocksmith;

import java.io.File;
import java.util.prefs.Preferences;

/**
 *
 * @author joostmeulenkamp
 */
public class Config {

    private static Config config;

    public static final boolean TYPE_SENSITIVE = true;
    public static final String XML_NAMESPACE = "btsxml";
    public static final String XML_FILE_EXTENSION = "btsxml";

    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(Config.class);
    private static final String PREF_LAST_DIRECTORY = "lastOpenedDirectory";
    private static final String PREF_SHOW_HELP = "showHelp";

    private Config() {
    }

    public static Config get() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    private static void loadConfig() {
        config = new Config();
    }


    public static File getLastOpenedDirectory() {
        String path = PREFERENCES.get(PREF_LAST_DIRECTORY, null);
        if (path != null) {
            File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                return file;
            }
        }
        return null;
    }

    public static void setLastOpenedDirectory(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            PREFERENCES.put(PREF_LAST_DIRECTORY, file.getPath());
        } else {
            PREFERENCES.put(PREF_LAST_DIRECTORY, file.getParent());
        }
    }

    public static boolean showHelpOnStartup() {
        boolean showWelcomeDialog = PREFERENCES.getBoolean(PREF_SHOW_HELP, true);
        return showWelcomeDialog;
    }

    public static void setShowHelpOnStartup(boolean show) {
        PREFERENCES.putBoolean(PREF_SHOW_HELP, show);
    }


}
