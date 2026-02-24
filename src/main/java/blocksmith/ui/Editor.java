package blocksmith.ui;

import blocksmith.ui.workspace.WorkspaceFactoryNew;
import btscore.workspace.WorkspaceModel;
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
public class Editor {
    
    private final WorkspaceFactoryNew factory;
    
    private final Map<String, WorkspaceModel> workspaces = new HashMap<>();
    private WorkspaceModel active;
    
    private final List<Consumer<WorkspaceModel>> listeners = new ArrayList<>();
    
    public Editor(WorkspaceFactoryNew factory) {
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
    
    public WorkspaceModel activeDocument() {
        return active;
    }
    
    public void setOnActiveDocumentChanged(Consumer<WorkspaceModel> listener) {
        listeners.add(listener);
    }
    
    private void onActiveDocumentChanged() {
        listeners.forEach(c -> c.accept(active));
    }
    
}
