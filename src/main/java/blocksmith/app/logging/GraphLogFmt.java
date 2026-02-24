package blocksmith.app.logging;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.BlockPosition;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.PortDef;
import java.util.Collection;
import java.util.UUID;

/**
 *
 * @author joost
 */
public final class GraphLogFmt {

    private GraphLogFmt() {
    }

    public static String block(BlockId id) {
        return shortId(id.value());
    }

    public static String blocks(Collection<BlockId> ids) {
        return ids.stream()
                .map(GraphLogFmt::block)
                .toList()
                .toString();
    }

    public static String port(PortRef ref) {
        return block(ref.blockId()) + "." + ref.valueId();
    }

    public static String connection(PortRef from, PortRef to) {
        return port(from) + " -> " + port(to);
    }

    public static String connection(Connection c) {
        return connection(c.from(), c.to());
    }

    public static String movedBlocks(Collection<BlockPosition> positions) {
        return positions.stream()
                .map(p -> block(p.id()))
                .toList()
                .toString();
    }

    public static String shortId(UUID id) {
        return id.toString().substring(0, 8);
    }

    public static String shortIdOrMissing(UUID id) {
        return id == null ? "<missing-id>" : shortId(id);
    }
}
