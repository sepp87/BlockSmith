package blocksmith.domain.connection;

import blocksmith.domain.block.BlockId;
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

    public static Connection of(
            BlockId fromBlock, String fromPort,
            BlockId toBlock, String toPort) {
        
        return new Connection(
                PortRef.output(fromBlock, fromPort),
                PortRef.input(toBlock, toPort)
        );
    }

}
