package blocksmith.app.block;

import blocksmith.app.clipboard.CopyMemory;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.group.Group;
import blocksmith.domain.group.GroupId;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author joost
 */
public class PasteBlocks {

    private final CopyMemory memory;

    public PasteBlocks(CopyMemory memory) {
        this.memory = memory;
    }

    public Graph execute(Graph target) {
        var copied = memory.getCopy().orElse(null);
        if (copied == null) {
            return target;
        }

        var newId = copied.blocks()
                .stream()
                .collect(Collectors.toMap(Block::id, b -> BlockId.create()));

        var blocks = copied.blocks()
                .stream()
                .map(b -> b.duplicate(newId.get(b.id())))
                .map(b -> b.withPosition(b.layout().x() + 20, b.layout().y() + 20)) // offset pasted blocks
                .toList();

        var connectionsWithoutDangling = copied.connections()
                .stream()
                .filter(c -> {
                    var from = c.from().blockId();
                    var isDangling = !newId.containsKey(from);
                    var pointsToMissingBlock = !target.block(from).isPresent();
                    return isDangling && pointsToMissingBlock ? false : true;
                })
                .toList();

        var connections = connectionsWithoutDangling
                .stream()
                .map(c -> {
                    var fromCopy = c.from().blockId();
                    var toCopy = c.to().blockId();
                    var fromNewId = c.from().withBlockId(newId.getOrDefault(fromCopy, fromCopy));
                    var toNewId = c.to().withBlockId(newId.getOrDefault(toCopy, toCopy));
                    return new Connection(fromNewId, toNewId);
                })
                .toList();

        var groups = copied.groups()
                .stream()
                .map(g -> {
                    var newIds = g.blocks().stream().map(b -> newId.get(b)).toList();
                    var groupId = GroupId.create();
                    return new Group(groupId, g.label(), newIds);
                })
                .toList();

        var mergedBlocks = Stream.concat(blocks.stream(), target.blocks().stream()).toList();
        var mergedConnections = Stream.concat(connections.stream(), target.connections().stream()).toList();
        var mergedGroups = Stream.concat(groups.stream(), target.groups().stream()).toList();

        return new Graph(target.id(), mergedBlocks, mergedConnections, mergedGroups);

    }

}
