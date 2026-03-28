package blocksmith;

import java.io.File;
import java.util.Properties;
import java.util.prefs.Preferences;
import blocksmith.infra.utils.FileUtils;
import blocksmith.utils.OperatingSystem;
import blocksmith.utils.SystemUtils;

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

    private static final String LIBRARY_DIRECTORY = "lib" + File.separatorChar;
    private static final String BUILD_DIRECTORY = "build" + File.separatorChar;
    private static final String CONFIG_DIRECTORY = "config" + File.separatorChar;
    private static final String SETTINGS_FILE = "settings.txt";
    private static final String ICONS_DIRECTORY = "fontawesome-svg" + File.separatorChar;

    private String appRootDirectory;
    private OperatingSystem operatingSystem;
    private Properties settings;

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
        config.appRootDirectory = SystemUtils.getAppRootDirectory(config, BUILD_DIRECTORY);

        FileUtils.createDirectory(new File(config.appRootDirectory + LIBRARY_DIRECTORY));
        FileUtils.createDirectory(new File(config.appRootDirectory + CONFIG_DIRECTORY));;
        File settingsFile = new File(config.appRootDirectory + CONFIG_DIRECTORY + SETTINGS_FILE);
        FileUtils.createFile(settingsFile);

        config.operatingSystem = SystemUtils.determineOperatingSystem();
        config.settings = FileUtils.loadProperties(settingsFile);
    }

    public String appRootDirectory() {
        return appRootDirectory;
    }

    public String libraryDirectory() {
        return appRootDirectory + LIBRARY_DIRECTORY;
    }

    public String iconsDirectory() {
        return ICONS_DIRECTORY;
    }

    public OperatingSystem operatingSystem() {
        return operatingSystem;
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
