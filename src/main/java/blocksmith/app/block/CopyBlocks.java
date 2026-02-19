package blocksmith.app.block;

import blocksmith.app.clipboard.CopyMemory;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.graph.Graph;
import java.util.Collection;
import java.util.Optional;

/**
 *
 * @author joost
 */
public class CopyBlocks {

    private final CopyMemory memory;

    public CopyBlocks(CopyMemory memory) {
        this.memory = memory;
    }

    public void execute(Graph graph, Collection<BlockId> ids) {

        var blocks = ids
                .stream()
                .map(id -> graph.block(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        // TODO test bug fix > if to is not contained then remove
        var connections = graph.connections()
                .stream()
//                .filter(c -> ids.contains(c.from().blockId()) || ids.contains(c.to().blockId()))
                .filter(c -> ids.contains(c.to().blockId()))
                .toList();

        var groups = graph.groups()
                .stream()
                .filter(g -> ids.containsAll(g.blocks()))
                .toList();

        var copy = new Graph(graph.id(), blocks, connections, groups);

        memory.add(copy);

    }

}
