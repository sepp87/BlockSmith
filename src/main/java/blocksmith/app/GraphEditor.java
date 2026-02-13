package blocksmith.app;

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
    private final AddConnection addConnection;
    private final RemoveConnection removeConnection;
    private final AddGroup addGroup;
    private final RemoveGroup removeGroup;

    public GraphEditor(
            Graph graph,
            AddBlock addBlock,
            RemoveBlock removeBlock,
            AddConnection addConnection,
            RemoveConnection removeConnection,
            AddGroup addGroup,
            RemoveGroup removeGroup) {

        this.graph = graph;
        this.addBlock = addBlock;
        this.removeBlock = removeBlock;
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
        if(updated == graph) {
            return;
        }
        undoStack.push(graph);
        redoStack.clear();
        graph = updated;
    }

    public void addBlock(String type, EditorMetadata metadata) {
        var id = BlockId.create();
        mutate((graph) -> addBlock.execute(graph, id, type));
        LOGGER.log(Level.INFO, "Add block: " + type);
    }

    public void removeBlock(BlockId id) {
        mutate((graph) -> removeBlock.execute(graph, id));
        LOGGER.log(Level.INFO, "Remove block: " + shortId(id.value()));
    }

    public void setParamValue(BlockId id, String valueId, String value) {
        LOGGER.log(Level.INFO, "Set param value for block: " + shortId(id.value()) + "." + valueId + "=" + value);

    }

    public void addConnection(PortRef from, PortRef to) {
        LOGGER.log(Level.INFO, "Add connection: {0}.{1} -> {2}.{3}",
                new Object[]{
                    shortId(from.blockId().value()), from.valueId(),
                    shortId(to.blockId().value()), to.valueId()}
        );
    }

    public void removeConnection(Connection connection) {
        LOGGER.log(Level.INFO, "Remove connection: {0}.{1} -> {2}.{3}",
                new Object[]{
                    shortId(connection.from().blockId().value()), connection.from().valueId(),
                    shortId(connection.to().blockId().value()), connection.to().valueId()}
        );
    }

    public void addGroup(String label, Collection<BlockId> blocks) {
        LOGGER.log(Level.INFO, "Add group: " + blocks);

    }

    public void removeGroup() {

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

}
