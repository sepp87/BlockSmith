package blocksmith.domain.graph;

import java.util.UUID;

/**
 *
 * @author joost
 */
public record GraphId(UUID value) {

    public static GraphId from(String id) {
        return new GraphId(UUID.fromString(id));
    }

    public static GraphId create() {
        return new GraphId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
