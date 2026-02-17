package blocksmith.app;

import blocksmith.app.block.AddBlock;
import blocksmith.app.connection.AddConnection;
import blocksmith.app.connection.RemoveConnection;
import blocksmith.app.group.AddGroup;
import blocksmith.app.group.RemoveGroup;
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
    private final RemoveGroup removeGroup;

    public GraphEditorFactory(
            AddBlock addBlock,
            AddConnection addConnection,
            RemoveConnection removeConnection,
            AddGroup addGroup,
            RemoveGroup removeGroup) {

        this.addBlock = addBlock;
        this.addConnection = addConnection;
        this.removeConnection = removeConnection;
        this.addGroup = addGroup;
        this.removeGroup = removeGroup;
    }

    public GraphEditor createDefault(Graph graph) {
        return new DefaultGraphEditor(
                graph,
                addBlock,
                addConnection,
                removeConnection,
                addGroup,
                removeGroup
        );
    }
}
