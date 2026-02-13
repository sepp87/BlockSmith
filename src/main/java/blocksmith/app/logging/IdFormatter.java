package blocksmith.app.logging;

import java.util.UUID;

/**
 *
 * @author joostmeulenkamp
 */
public final class IdFormatter {

    private IdFormatter() {

    }

    public static String shortId(UUID id) {
        return id.toString().substring(0, 8);
    }

    public static String shortIdOrMissing(UUID id) {
        return id == null ? "<missing-id>" : shortId(id);
    }
}
