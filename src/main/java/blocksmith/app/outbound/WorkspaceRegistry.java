package blocksmith.app.outbound;

/**
 *
 * @author joostmeulenkamp
 */
public interface WorkspaceRegistry {

    void add(WorkspaceHandle workspace);

    void remove(String id);

    void activate(String id);
    
    WorkspaceHandle active();
}
