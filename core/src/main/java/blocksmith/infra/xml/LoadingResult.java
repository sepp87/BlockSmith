package blocksmith.infra.xml;

import blocksmith.app.GraphDocument;
import blocksmith.app.logging.GraphLogFmt;
import blocksmith.domain.block.UnknownBlock;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.Port;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joost
 */
public record LoadingResult(GraphDocument document, List<String> errors) {
    
    private static final String CONNECTION_FAILED = "ERROR: Missing port, could NOT create connection %s: %s";
    private static final String BLOCK_FAILED = "ERROR: Unknown block type: %s, with id: %s";
    
    public static LoadingResult of(
            GraphDocument document,
            List<UnknownBlock> unknownBlocks,
            List<PortRef> missingPorts) {
        
        var errors = new ArrayList<String>();
        for (var block : unknownBlocks) {
            var msg = BLOCK_FAILED.formatted(block.type(), GraphLogFmt.block(block.id()));
            errors.add(msg);
        }
        for (var port : missingPorts) {
            var msg = CONNECTION_FAILED.formatted(port.direction() == Port.Direction.INPUT ? "from" : "to", GraphLogFmt.port(port));
            errors.add(msg);
        }
        
        return new LoadingResult(document, List.of());
    }
    
}
