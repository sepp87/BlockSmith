package blocksmith.utils;

/**
 *
 * @author Joost
 */
public class SystemUtils {
    
    private static final OperatingSystem OPERATING_SYSTEM;
    
    static {
        OPERATING_SYSTEM = determineOperatingSystem();
    }
    
    public static OperatingSystem operatingSystem() {
        return OPERATING_SYSTEM;
    }

    private static OperatingSystem determineOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OperatingSystem.WINDOWS;
        } else if (osName.contains("mac")) {
            return OperatingSystem.MACOS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return OperatingSystem.LINUX;
        } else if (osName.contains("sunos")) {
            return OperatingSystem.SOLARIS;
        } else {
            return OperatingSystem.OTHER_OS;
        }
    }


}
