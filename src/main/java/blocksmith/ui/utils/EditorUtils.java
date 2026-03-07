package blocksmith.ui.utils;

import blocksmith.ui.UiApp;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import blocksmith.ui.editor.EditorView;
import blocksmith.ui.graph.group.BlockGroupView;
import blocksmith.ui.workspace.Background;
import blocksmith.ui.workspace.WorkspaceView;

/**
 *
 * @author Joost
 */
public class EditorUtils {

    public static boolean onFreeSpace(MouseEvent event) {
        Node intersectedNode = event.getPickResult().getIntersectedNode();
        if (UiApp.LOG_FREE_SPACE_CHECK) {
            System.out.println("CLICKED ON: " + intersectedNode.getClass().getSimpleName());

        }

        return intersectedNode instanceof EditorView || intersectedNode instanceof WorkspaceView || intersectedNode instanceof BlockGroupView || intersectedNode instanceof Background;
    }
}
