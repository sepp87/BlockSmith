package blocksmith.app.workspace;

import blocksmith.app.outbound.WorkspaceFactory;
import blocksmith.app.outbound.WorkspaceRegistry;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class WorkspaceLifecycle {

    private static final Logger LOGGER = Logger.getLogger(WorkspaceLifecycle.class.getName());
    
    private final WorkspaceFactory factory;
    private final WorkspaceRegistry registry;

    public WorkspaceLifecycle(WorkspaceFactory workspaceFactory, WorkspaceRegistry workspaceRegistry) {
        this.factory = workspaceFactory;
        this.registry = workspaceRegistry;
    }

    public void newDocument() {
        var workspace = factory.newDocument();
        registry.add(workspace);
    }

    public void openDocument(Path path) {
        try {
            var workspace = factory.openDocument(path);
            registry.add(workspace);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Document could NOT be opened: " + path, ex);
        }
    }

}
