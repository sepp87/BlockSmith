package blocksmith.ui;

import blocksmith.app.block.MoveBlockRequest;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.EditorMetadata;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import btscore.workspace.WorkspaceModel;
import java.util.Collection;
import blocksmith.app.inbound.GraphMutation;
import blocksmith.app.inbound.GraphDesignSession;
import blocksmith.app.inbound.GraphHistory;

/**
 *
 * @author joost
 */
public class WorkspaceSession implements GraphMutation, GraphHistory {

    private final GraphDesignSession editor;
    private final WorkspaceModel workspace;

    public WorkspaceSession(GraphDesignSession editor, WorkspaceModel workspace) {
        this.editor = editor;
        this.workspace = workspace;
    }

    private void execute(Runnable action) {
        action.run();
        updateProjection();
    }

    private void updateProjection() {
        var graph = editor.currentGraph();
        workspace.updateFrom(graph);
    }

    @Override
    public void addBlock(String type, EditorMetadata metadata) {
        execute(() -> editor.addBlock(type, metadata));
    }

    @Override
    public void removeBlock(BlockId id) {
        execute(() -> editor.removeBlock(id));
    }

    @Override
    public void removeAllBlocks(Collection<BlockId> blocks) {
        execute(() -> editor.removeAllBlocks(blocks));
    }

    @Override
    public void moveBlocks(Collection<MoveBlockRequest> requests) {
        execute(() -> editor.moveBlocks(requests));
    }

    @Override
    public void setParamValue(BlockId id, String valueId, String value) {
        execute(() -> editor.setParamValue(id, valueId, value));
    }

    @Override
    public void addConnection(PortRef from, PortRef to) {
        execute(() -> editor.addConnection(from, to));
    }

    @Override
    public void removeConnection(Connection connection) {
        execute(() -> editor.removeConnection(connection));
    }

    @Override
    public void addGroup(String label, Collection<BlockId> blocks) {
        execute(() -> editor.addGroup(label, blocks));
    }

    @Override
    public void undo() {
        editor.undo();
        updateProjection();
    }

    @Override
    public void redo() {
        editor.redo();
        updateProjection();
    }

    public boolean hasUndoableState() {
        return hasUndoableState();
    }

    public boolean hasRedoableState() {
        return hasRedoableState();
    }

}
