package blocksmith.app.outbound;

import java.nio.file.Path;

/**
 *
 * @author joostmeulenkamp
 */
public interface WorkspaceFactory {

    WorkspaceHandle newDocument();

    WorkspaceHandle openDocument(Path path) throws Exception;

}
