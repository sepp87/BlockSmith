package blocksmith.app;

import blocksmith.app.connection.AddConnection;
import blocksmith.app.connection.RemoveConnection;
import blocksmith.app.group.RemoveGroup;
import blocksmith.app.group.AddGroup;
import blocksmith.app.block.RemoveBlock;
import blocksmith.app.block.AddBlock;
import blocksmith.app.block.MoveBlockRequest;
import blocksmith.app.block.MoveBlocks;
import blocksmith.app.block.RemoveAllBlocks;
import blocksmith.app.block.SetParamValue;
import blocksmith.app.inbound.GraphDesignSession;
import static blocksmith.app.logging.IdFormatter.shortId;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.EditorMetadata;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class GraphEditor implements GraphDesignSession {

    private static final Logger LOGGER = Logger.getLogger(GraphDesignSession.class.getName());

    private Graph graph;
    private final ArrayDeque<Graph> undoStack = new ArrayDeque<>();
    private final ArrayDeque<Graph> redoStack = new ArrayDeque<>();

    private final AddBlock addBlock;
    private final RemoveBlock removeBlock;
    private final RemoveAllBlocks removeAllBlocks;
    private final SetParamValue setParamValue;
    private final MoveBlocks moveBlocks;
    private final AddConnection addConnection;
    private final RemoveConnection removeConnection;
    private final AddGroup addGroup;
    private final RemoveGroup removeGroup;

    public GraphEditor(
            Graph graph,
            AddBlock addBlock,
            RemoveBlock removeBlock,
            RemoveAllBlocks removeAllBlocks,
            SetParamValue setParamValue,
            MoveBlocks moveBlocks,
            AddConnection addConnection,
            RemoveConnection removeConnection,
            AddGroup addGroup,
            RemoveGroup removeGroup) {

        this.graph = graph;
        this.addBlock = addBlock;
        this.removeBlock = removeBlock;
        this.removeAllBlocks = removeAllBlocks;
        this.setParamValue = setParamValue;
        this.moveBlocks = moveBlocks;
        this.addConnection = addConnection;
        this.removeConnection = removeConnection;
        this.addGroup = addGroup;
        this.removeGroup = removeGroup;
    }

    public Graph currentGraph() {
        return graph;
    }

    private void mutate(Function<Graph, Graph> action) {
        var updated = action.apply(graph);
        if (updated == graph) {
            return;
        }
        undoStack.push(graph);
        redoStack.clear();
        graph = updated;
    }

    public void addBlock(String type, EditorMetadata metadata) {
        var id = BlockId.create();
        mutate((graph) -> addBlock.execute(graph, id, type));
        LOGGER.log(Level.INFO, "Add block: {0} {1}",
                new Object[]{shortId(id.value()), type}
        );
    }

    public void removeBlock(BlockId id) {
        mutate((graph) -> removeBlock.execute(graph, id));
        LOGGER.log(Level.INFO, "Remove block: {0}", shortId(id.value()));
    }

    public void removeAllBlocks(Collection<BlockId> blocks) {
        mutate((graph) -> removeAllBlocks.execute(graph, blocks));
        var shortIds = blocks.stream().map(id -> shortId(id.value())).toList();
        LOGGER.log(Level.INFO, "Remove all blocks: {0}", shortIds.toString());
    }

    public void setParamValue(BlockId id, String valueId, String value) {
        LOGGER.log(Level.INFO, "Set param value for block: {0}.{1}={2}",
                new Object[]{shortId(id.value()), valueId, value}
        );
    }

    public void moveBlocks(Collection<MoveBlockRequest> requests) {
        mutate((graph) -> moveBlocks.execute(graph, requests));
        var shortIds = requests.stream().map(r -> shortId(r.id().value())).toList();
        LOGGER.log(Level.INFO, "Move blocks: {0}", shortIds.toString());
    }

    public void addConnection(PortRef from, PortRef to) {
        mutate((graph) -> addConnection.execute(graph, from, to));
        LOGGER.log(Level.INFO, "Add connection: {0}.{1} -> {2}.{3}",
                new Object[]{
                    shortId(from.blockId().value()), from.valueId(),
                    shortId(to.blockId().value()), to.valueId()}
        );
    }

    public void removeConnection(Connection connection) {
        mutate((graph) -> removeConnection.execute(graph, connection));
        LOGGER.log(Level.INFO, "Remove connection: {0}.{1} -> {2}.{3}",
                new Object[]{
                    shortId(connection.from().blockId().value()), connection.from().valueId(),
                    shortId(connection.to().blockId().value()), connection.to().valueId()}
        );
    }

    public void addGroup(String label, Collection<BlockId> blocks) {
        mutate((graph) -> addGroup.execute(graph, label, blocks));
        var shortIds = blocks.stream().map(id -> shortId(id.value())).toList();
        LOGGER.log(Level.INFO, "Add group: {0}", shortIds.toString());
    }

    public void removeGroup(String label, Collection<BlockId> blocks) {
        mutate((graph) -> removeGroup.execute(graph, label, blocks));
        var shortIds = blocks.stream().map(id -> shortId(id.value())).toList();
        LOGGER.log(Level.INFO, "Remove group: {0}", shortIds.toString());
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        var currentGraph = graph;
        redoStack.push(currentGraph);

        var previousGraph = undoStack.pop();
        graph = previousGraph;
    }

    public void redo() {
        if (redoStack.isEmpty()) {
            return;
        }
        var currentGraph = graph;
        undoStack.push(currentGraph);

        var nextGraph = redoStack.pop();
        graph = nextGraph;
    }

    public boolean hasUndoableState() {
        return !undoStack.isEmpty();
    }

    public boolean hasRedoableState() {
        return !redoStack.isEmpty();
    }

}
