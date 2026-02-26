package blocksmith.domain.group;

import java.util.UUID;

/**
 *
 * @author joost
 */
public record GroupId(UUID value) {

    public static GroupId from(String id) {
        return new GroupId(UUID.fromString(id));
    }

    public static GroupId create() {
        return new GroupId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
