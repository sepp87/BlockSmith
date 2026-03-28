package blocksmith.ui.display;

import blocksmith.ui.display.ValueDisplay.SingleValue;
import blocksmith.domain.value.types.DataSheet;
import blocksmith.ui.display.utils.DataSheetViewer;
import javafx.scene.Node;

/**
 *
 * @author joost
 */
public class DataSheetDisplay implements SingleValue {

    private final DataSheetViewer viewer;

    public DataSheetDisplay() {
        this.viewer = new DataSheetViewer();
    }

    @Override
    public void render(Object value) {
        var sheet = value == null ? null : (DataSheet) value;
        viewer.setDataSheet(sheet, true);
    }

    @Override
    public Node node() {
        return viewer;
    }

    @Override
    public void dispose() {
    }

}
