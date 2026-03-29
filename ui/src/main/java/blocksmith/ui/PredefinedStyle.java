package blocksmith.ui;

import java.util.Optional;

/**
 *
 * @author joost
 */
public enum PredefinedStyle {
    DARK("css/dark_mode.css"),
    LIGHT("css/flat_white.css"),
    SINGER("css/flat_singer.css");

    private final String path;

    PredefinedStyle(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    public static Optional<PredefinedStyle> fromPath(String path) {
        for (var style : values()) {
            if (style.path.equals(path)) {
                return Optional.of(style);
            }
        }
        return Optional.empty();
    }
    
    public static boolean contains(String path ) {
        return fromPath(path).isPresent();
    }
}
