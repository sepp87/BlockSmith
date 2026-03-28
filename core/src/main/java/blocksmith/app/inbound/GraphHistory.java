package blocksmith.app.inbound;

/**
 *
 * @author joostmeulenkamp
 */
public interface GraphHistory {

    void undo();

    void redo();

    boolean hasUndoableState();

    boolean hasRedoableState();

}
