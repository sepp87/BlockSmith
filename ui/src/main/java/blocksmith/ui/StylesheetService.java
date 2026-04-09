package blocksmith.ui;

import blocksmith.infra.utils.PathWatcher;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
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
    private Function<String, String> cssPathOverride;

    private String activeCss;
    private final List<Consumer<String>> cssListeners = new ArrayList<>();
    private Thread cssWatcher;

    private StylesheetService(Function<String, String> cssPathOverride) {
        this.watchPredefinedAlso = cssPathOverride != null;
        this.cssPathOverride = cssPathOverride;
        this.activeCss = lastCssOrDefault();

        startWatcher();
    }

    private void startWatcher() {
        cssWatcher = watchPredefinedAlso
                ? watchStyle(activeCss)
                : watchUserDefinedOnly(activeCss);
    }

    private Thread watchUserDefinedOnly(String css) {
        if (PredefinedStyle.contains(css)) {
            return null;
        }
        return watchStyle(css);
    }

    private Thread watchStyle(String css) {
        var url = toUrl(css);
        var path = Path.of(URI.create(url));
        return PathWatcher.watchFile(path, __ -> onCssUpdated());
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
            if (watchPredefinedAlso) {
                return Path.of(cssPathOverride.apply(rawPath)).toUri().toString();
            }
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

    private void stopWatcher() {
        if (cssWatcher != null) {
            cssWatcher.interrupt();
            cssWatcher = null;
        }
    }

    public void setOnCssUpdated(Consumer<String> listener) {
        cssListeners.clear();
        cssListeners.add(listener);
    }

    private void onCssUpdated() {
        String url = toUrl(activeCss);
        cssListeners.forEach(c -> c.accept(url));
    }

    public static StylesheetService forDev(String resourcesDirectory) {
        return new StylesheetService(css -> resourcesDirectory + css);
    }

    public static StylesheetService forProd() {
        return new StylesheetService(null);
    }

}
