package blocksmith.exec;

/**
 *
 * @author joost
 */
public record BlockException(
        String index,
        Severity severity,
        Throwable exception) {

    public enum Severity {
        WARNING,
        ERROR
    }
}
