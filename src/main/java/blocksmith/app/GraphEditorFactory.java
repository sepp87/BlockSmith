package blocksmith.app;

import blocksmith.app.block.AddBlock;
import blocksmith.app.block.CopyBlocks;
import blocksmith.app.block.PasteBlocks;
import blocksmith.app.connection.AddConnection;
import blocksmith.app.connection.RemoveConnection;
import blocksmith.app.group.AddGroup;
import blocksmith.app.inbound.GraphEditor;
import blocksmith.domain.graph.Graph;

/**
 *
 * @author joost
 */
public class GraphEditorFactory {

    private final AddBlock addBlock;
    private final AddConnection addConnection;
    private final RemoveConnection removeConnection;
    private final AddGroup addGroup;
    private final CopyBlocks copyBlocks;
    private final PasteBlocks pasteBlocks;

    public GraphEditorFactory(
            AddBlock addBlock,
            AddConnection addConnection,
            RemoveConnection removeConnection,
            AddGroup addGroup,
            CopyBlocks copyBlocks,
            PasteBlocks pasteBlocks) {

        this.addBlock = addBlock;
        this.addConnection = addConnection;
        this.removeConnection = removeConnection;
        this.addGroup = addGroup;
        this.copyBlocks = copyBlocks;
        this.pasteBlocks = pasteBlocks;
    }

    public GraphEditor createDefault(Graph graph) {
        return new DefaultGraphEditor(
                graph,
                addBlock,
                addConnection,
                removeConnection,
                addGroup,
                copyBlocks,
                pasteBlocks
        );
    }
}
