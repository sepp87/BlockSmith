package blocksmith.exec.block;

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
        ERROR,
        CRITICAL
    }
    
    public static BlockException critical(Throwable exception) {
        return new BlockException(null, Severity.CRITICAL, exception);
    }
    
}
