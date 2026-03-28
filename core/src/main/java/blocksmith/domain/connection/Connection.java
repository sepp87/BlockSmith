package blocksmith.domain.connection;

import java.util.Objects;

/**
 *
 * @author joostmeulenkamp
 */
public record Connection(PortRef from, PortRef to) {

    public Connection  {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
    }

}
