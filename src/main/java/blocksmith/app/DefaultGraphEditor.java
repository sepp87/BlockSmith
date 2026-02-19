package blocksmith.app;

import blocksmith.app.connection.AddConnection;
import blocksmith.app.connection.RemoveConnection;
import blocksmith.app.group.RemoveGroup;
import blocksmith.app.group.AddGroup;
import blocksmith.app.block.AddBlock;
import blocksmith.domain.block.BlockPosition;
import static blocksmith.app.logging.IdFormatter.shortId;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import blocksmith.app.inbound.GraphEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author joost
 */
public class DefaultGraphEditor implements GraphEditor {

    private static final Logger LOGGER = Logger.getLogger(GraphEditor.class.getName());

    private Graph graph;
    private final List<Consumer<Graph>> listeners = new ArrayList<>();
    private final ArrayDeque<Graph> undoStack = new ArrayDeque<>();
    private final ArrayDeque<Graph> redoStack = new ArrayDeque<>();

    private final AddBlock addBlock;
    private final AddConnection addConnection;
    private final RemoveConnection removeConnection;
    private final AddGroup addGroup;
    private final RemoveGroup removeGroup;

    public DefaultGraphEditor(
            Graph graph,
            AddBlock addBlock,
            AddConnection addConnection,
            RemoveConnection removeConnection,
            AddGroup addGroup,
            RemoveGroup removeGroup) {

        this.graph = graph;
        this.addBlock = addBlock;
        this.addConnection = addConnection;
        this.removeConnection = removeConnection;
        this.addGroup = addGroup;
        this.removeGroup = removeGroup;
    }

    public Graph currentGraph() {
        return graph;
    }

    public void setOnGraphUpdated(Consumer<Graph> listener) {
        listeners.add(listener);
    }

    private void notifyGraphUpdated(Graph updated) {
        listeners.forEach(c -> c.accept(graph));
    }

    private void mutate(Function<Graph, Graph> action) {
        var updated = action.apply(graph);
        if (updated == graph) {
            return;
        }
        undoStack.push(graph);
        redoStack.clear();
        graph = updated;
        notifyGraphUpdated(updated);
    }

    public void addBlock(String type, double x, double y) {
        var id = BlockId.create();
        mutate((graph) -> addBlock.execute(graph, id, type, x, y));
        LOGGER.log(Level.INFO, "Add block: {0} {1}",
                new Object[]{shortId(id.value()), type}
        );
    }

    public void removeBlock(BlockId id) {
        mutate((graph) -> graph.withoutBlock(id));
        LOGGER.log(Level.INFO, "Remove block: {0}", shortId(id.value()));
    }

    public void removeAllBlocks(Collection<BlockId> blocks) {
        mutate((graph) -> graph.withoutBlocks(blocks));
        var shortIds = blocks.stream().map(id -> shortId(id.value())).toList();
        LOGGER.log(Level.INFO, "Remove all blocks: {0}", shortIds.toString());
    }

    public void updateParamValue(BlockId id, String valueId, String value) {
        mutate((graph) -> graph.updateParamValue(id, valueId, value));
        LOGGER.log(Level.INFO, "Set param value for block: {0}.{1}={2}",
                new Object[]{shortId(id.value()), valueId, value}
        );
    }

    public void moveBlocks(Collection<BlockPosition> positions) {
        mutate((graph) -> graph.moveBlocks(positions));
        var shortIds = positions.stream().map(r -> shortId(r.id().value())).toList();
        LOGGER.log(Level.INFO, "Move blocks: {0}", shortIds.toString());
    }

    public void resizeBlock(BlockId id, double width, double height) {
        mutate((graph) -> graph.resizeBlock(id, width, height));
        LOGGER.log(Level.INFO, "Resize block: {0}, width: {1}, height: {2} ",
                new Object[]{shortId(id.value()), width, height}
        );
    }

    public void addConnection(PortRef from, PortRef to) {
        LOGGER.log(Level.INFO, "Add connection: {0}.{1} -> {2}.{3}",
                new Object[]{
                    shortId(from.blockId().value()), from.valueId(),
                    shortId(to.blockId().value()), to.valueId()}
        );
        mutate((graph) -> addConnection.execute(graph, from, to));

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

    public void copyGraph(Collection<BlockId> blocks) {

    }

    public void pasteGraph() {

    }

    public void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        var currentGraph = graph;
        redoStack.push(currentGraph);

        var previousGraph = undoStack.pop();
        graph = previousGraph;
        notifyGraphUpdated(previousGraph);

    }

    public void redo() {
        if (redoStack.isEmpty()) {
            return;
        }
        var currentGraph = graph;
        undoStack.push(currentGraph);

        var nextGraph = redoStack.pop();
        graph = nextGraph;
        notifyGraphUpdated(nextGraph);

    }

    public boolean hasUndoableState() {
        return !undoStack.isEmpty();
    }

    public boolean hasRedoableState() {
        return !redoStack.isEmpty();
    }

}
