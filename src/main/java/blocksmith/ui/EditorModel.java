package blocksmith.ui;

import blocksmith.ui.workspace.WorkspaceSessionFactory;
import btscore.workspace.WorkspaceSession;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author joost
 */
public class EditorModel {
    
    private final WorkspaceSessionFactory factory;
    
    private final Map<String, WorkspaceSession> workspaces = new HashMap<>();
    private WorkspaceSession active;
    
    private final List<Consumer<WorkspaceSession>> listeners = new ArrayList<>();
    
    public EditorModel(WorkspaceSessionFactory factory) {
        this.factory = factory;
    }
    
    public void newDocument() {
        var workspace = factory.newDocument();
        if (active != null) {
            // dispose
        }
        active = workspace;
        onActiveDocumentChanged();
    }
    
    public void openDocument(Path path) throws Exception {
        var workspace = factory.openDocument(path);
        if (active != null) {
            // dispose
        }
        active = workspace;
        onActiveDocumentChanged();
    }
    
    public WorkspaceSession activeDocument() {
        return active;
    }
    
    public void setOnActiveDocumentChanged(Consumer<WorkspaceSession> listener) {
        listeners.add(listener);
    }
    
    private void onActiveDocumentChanged() {
        listeners.forEach(c -> c.accept(active));
    }
    
}
