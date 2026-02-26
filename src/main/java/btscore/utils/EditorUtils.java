package btscore.utils;

import btscore.UiApp;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import btscore.editor.EditorView;
import btscore.graph.group.BlockGroupView;
import btscore.workspace.Background;
import btscore.workspace.WorkspaceView;

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
