package blocksmith.domain.block;

import java.util.UUID;

/**
 *
 * @author joost
 */
public record BlockId(UUID value) {

    public static BlockId from(String id) {
        return new BlockId(UUID.fromString(id));
    }

    public static BlockId create() {
        return new BlockId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
