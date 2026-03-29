package blocksmith.ui;

import blocksmith.infra.utils.PathWatcher;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

/**
 *
 * @author joost
 */
public class StylesheetService {

    private static final Preferences PREFS = Preferences.userNodeForPackage(StylesheetService.class);
    private static final String PREFS_KEY = "stylesheet";

    private static final PredefinedStyle DEFAULT_STYLESHEET = PredefinedStyle.LIGHT;

    private final boolean watchPredefinedAlso;
    private String activeCss;
    private final List<Consumer<String>> cssListeners = new ArrayList<>();
    private Thread cssWatcher;

    private StylesheetService(boolean watchPredefinedAlso) {
        this.watchPredefinedAlso = watchPredefinedAlso;
        this.activeCss = lastCssOrDefault();

        startWatcher();
    }

    private Thread watchStyle(String css) {
        return PathWatcher.watchFile(Path.of(css), __ -> onCssUpdated());
    }

    private Thread watchUserDefinedOnly(String css) {
        if (PredefinedStyle.contains(css)) {
            return null;
        }
        return PathWatcher.watchFile(Path.of(css), __ -> onCssUpdated());
    }

    private String lastCssOrDefault() {
        String raw = PREFS.get(PREFS_KEY, DEFAULT_STYLESHEET.path());
        if (PredefinedStyle.contains(raw)) {
            return raw;
        }
        return cssOrFallback(raw, DEFAULT_STYLESHEET.path());
    }

    private String cssOrFallback(String css, String fallback) {
        Path candidate = Path.of(css);
        if (Files.exists(candidate) && css.toLowerCase().endsWith(".css")) {
            return css;
        }
        return fallback;
    }

    public String getCss() {
        return toUrl(activeCss);
    }

    private String toUrl(String rawPath) {
        if (PredefinedStyle.contains(rawPath)) {
            return StylesheetService.class.getClassLoader().getResource(rawPath).toExternalForm();
        } else {
            return Path.of(rawPath).toUri().toString();
        }
    }

    public void setStyle(PredefinedStyle style) {
        var raw = style.path();
        applyCss(raw);
    }

    public void setCss(Path css) {
        var raw = cssOrFallback(css.toString(), activeCss);
        applyCss(raw);
    }

    private void applyCss(String css) {
        if (activeCss.equals(css)) {
            return;
        }
        PREFS.put(PREFS_KEY, css);
        activeCss = css;
        stopWatcher();
        startWatcher();
        onCssUpdated();
    }

    public void setOnCssUpdated(Consumer<String> listener) {
        cssListeners.clear();
        cssListeners.add(listener);
    }

    private void startWatcher() {
        cssWatcher = watchPredefinedAlso
                ? watchStyle(activeCss)
                : watchUserDefinedOnly(activeCss);
    }

    private void stopWatcher() {
        if (cssWatcher != null) {
            cssWatcher.interrupt();
            cssWatcher = null;
        }
    }

    private void onCssUpdated() {
        String url = toUrl(activeCss);
        cssListeners.forEach(c -> c.accept(url));
    }

    public static StylesheetService forDev() {
        return new StylesheetService(true);
    }

    public static StylesheetService forProd() {
        return new StylesheetService(false);
    }

}
