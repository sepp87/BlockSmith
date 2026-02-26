package blocksmith.app;

import blocksmith.app.connection.AddConnection;
import blocksmith.app.connection.RemoveConnection;
import blocksmith.app.group.AddGroup;
import blocksmith.app.block.AddBlock;
import blocksmith.app.block.CopyBlocks;
import blocksmith.app.block.PasteBlocks;
import blocksmith.domain.block.BlockPosition;
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
import blocksmith.app.logging.GraphLogFmt;
import blocksmith.domain.block.Block;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.group.GroupId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 *
 * @author joost
 */
public class DefaultGraphEditor implements GraphEditor {

    private static final Logger LOGGER = Logger.getLogger(GraphEditor.class.getName());

    private Graph graph;
    private final List<BiConsumer<Graph, Graph>> listeners = new ArrayList<>();
    private final ArrayDeque<Graph> undoStack = new ArrayDeque<>();
    private final ArrayDeque<Graph> redoStack = new ArrayDeque<>();

    private final AddBlock addBlock;
    private final AddConnection addConnection;
    private final RemoveConnection removeConnection;
    private final AddGroup addGroup;
    private final CopyBlocks copyBlocks;
    private final PasteBlocks pasteBlocks;

    public DefaultGraphEditor(
            Graph graph,
            AddBlock addBlock,
            AddConnection addConnection,
            RemoveConnection removeConnection,
            AddGroup addGroup,
            CopyBlocks copyBlocks,
            PasteBlocks pasteBlocks) {

        this.graph = graph;
        this.addBlock = addBlock;
        this.addConnection = addConnection;
        this.removeConnection = removeConnection;
        this.addGroup = addGroup;
        this.copyBlocks = copyBlocks;
        this.pasteBlocks = pasteBlocks;
    }

    public Graph graphSnapshot() {
        return graph;
    }

    public void setOnGraphUpdated(BiConsumer<Graph, Graph> listener) {
        listeners.add(listener);
    }

    private void graphUpdated(Graph oldGraph, Graph newGraph) {
        listeners.forEach(c -> c.accept(oldGraph, newGraph));
    }

    private void mutate(Function<Graph, Graph> action) {
        var newGraph = action.apply(graph);
        if (newGraph == graph) {
            return;
        }
        undoStack.push(graph);
        redoStack.clear();
        var oldGraph = graph;
        graph = newGraph;
        graphUpdated(oldGraph, newGraph);
    }

    public void addBlock(String type, double x, double y) {
        var id = BlockId.create();
        mutate((graph) -> addBlock.execute(graph, id, type, x, y));
        LOGGER.log(Level.INFO, "Add block: {0} {1}",
                new Object[]{GraphLogFmt.block(id), type}
        );
    }

    public void removeBlock(BlockId id) {
        mutate((graph) -> graph.withoutBlock(id));
        LOGGER.log(Level.INFO, "Remove block: {0}", GraphLogFmt.block(id));
    }

    public void removeAllBlocks(Collection<BlockId> blocks) {
        mutate((graph) -> graph.withoutBlocks(blocks));
        LOGGER.log(Level.INFO, "Remove all blocks: {0}", GraphLogFmt.blockIds(blocks));
    }

    public void updateParamValue(BlockId id, String valueId, String value) {
        mutate((graph) -> graph.updateParamValue(id, valueId, value));
        LOGGER.log(Level.INFO, "Set param value for block: {0}.{1}={2}",
                new Object[]{GraphLogFmt.block(id), valueId, value}
        );
    }

    public void moveBlocks(Collection<BlockPosition> positions) {
        mutate((graph) -> graph.moveBlocks(positions));
        LOGGER.log(Level.INFO, "Move blocks: {0}", GraphLogFmt.movedBlocks(positions));
    }

    public void resizeBlock(BlockId id, double width, double height) {
        mutate((graph) -> graph.resizeBlock(id, width, height));
        LOGGER.log(Level.INFO, "Resize block: {0}, width: {1}, height: {2} ",
                new Object[]{GraphLogFmt.block(id), width, height}
        );
    }

    public void addConnection(PortRef from, PortRef to) {
        LOGGER.log(Level.INFO, "Add connection: {0}", GraphLogFmt.connection(from, to));

        mutate((graph) -> addConnection.execute(graph, from, to));

    }

    public void removeConnection(Connection connection) {
        mutate((graph) -> removeConnection.execute(graph, connection));
        LOGGER.log(Level.INFO, "Remove connection: {0}", GraphLogFmt.connection(connection));
    }

    public void addGroup(String label, Collection<BlockId> blocks) {
        var id = GroupId.create();
        mutate((graph) -> addGroup.execute(graph, id, label, blocks));
        LOGGER.log(Level.INFO, "Add group: {0}", GraphLogFmt.blockIds(blocks));
    }

    public void removeGroup(GroupId id) {
        mutate((graph) -> graph.withoutGroup(id));
        LOGGER.log(Level.INFO, "Remove group: {0}", GraphLogFmt.group(id));
    }

    /**
     *
     * @param blocks to copy to the shared copy memory
     */
    public void copyBlocks(Collection<BlockId> blocks) {
        copyBlocks.execute(graph, blocks);
        LOGGER.log(Level.INFO, "Copy blocks: {0}", GraphLogFmt.blockIds(blocks));
    }

    /**
     * paste blocks copied to the shared copy memory
     */
    public Collection<BlockId> pasteBlocks() {
        var oldGraph = graph;
        mutate((graph) -> pasteBlocks.execute(graph));
        var diff = GraphDiff.compare(oldGraph, graph);
        LOGGER.log(Level.INFO, "Paste blocks: <copied-blocks>, Connections: {0}", GraphLogFmt.connections(diff.addedConnections()));
        return diff.addedBlocks().stream().map(Block::id).toList();
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        var currentGraph = graph;
        redoStack.push(currentGraph);

        var previousGraph = undoStack.pop();
        graph = previousGraph;
        graphUpdated(currentGraph, previousGraph);

    }

    public void redo() {
        if (redoStack.isEmpty()) {
            return;
        }
        var currentGraph = graph;
        undoStack.push(currentGraph);

        var nextGraph = redoStack.pop();
        graph = nextGraph;
        graphUpdated(currentGraph, nextGraph);

    }

    public boolean hasUndoableState() {
        return !undoStack.isEmpty();
    }

    public boolean hasRedoableState() {
        return !redoStack.isEmpty();
    }

}
