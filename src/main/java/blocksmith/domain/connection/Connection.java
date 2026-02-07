package blocksmith.domain.connection;

import blocksmith.domain.block.Port;
import java.util.Objects;

/**
 *
 * @author joostmeulenkamp
 */
public record Connection(PortRef from, PortRef to) {

    public Connection  {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        if (from.direction() != Port.Direction.OUTPUT) {
            throw new IllegalArgumentException("Connection must come from an OUTPUT port");
        }
        if (to.direction() != Port.Direction.INPUT) {
            throw new IllegalArgumentException("Connection must go towards an INPUT port");
        }
    }

}
