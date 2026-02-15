package btscore.editor;

import btscore.editor.context.EditorContext;
import btscore.editor.context.EditorEventRouter;
import btscore.workspace.WorkspaceContext;

/**
 *
 * @author joost
 */
public class BaseEditorController {

    protected final EditorContext editorContext;
    

    public BaseEditorController(EditorEventRouter editorEventRouter, EditorContext editorContext) {
        this.editorContext = editorContext;
    }

    protected WorkspaceContext activeWorkspaceContext() {
        return editorContext.activeWorkspace();
    }
}
