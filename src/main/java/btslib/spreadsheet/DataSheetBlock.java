package btslib.spreadsheet;

import blocksmith.domain.value.types.DataSheet;
import blocksmith.domain.value.ValueType;
import javafx.scene.layout.Region;
import blocksmith.ui.graph.block.BlockModel;
import blocksmith.infra.blockloader.annotations.Block;

/**
 *
 * @author joostmeulenkamp
 */
@Block(
        type = "Spreadsheet.tableView",
        category = "Spreadsheet",
        description = "View the spreadsheet data in the data sheet with this table view.",
        tags = {"tableView", "spreadsheet"})
public class DataSheetBlock extends BlockModel {

    private DataSheetViewer dataSheetViewer;

    public DataSheetBlock() {
//    public DataSheetBlock(WorkspaceModel workspace) {
//        super(workspace);
        labelProperty().set("Table");
        resizableProperty().set(true);
        addInputPort("data", "data", ValueType.of(DataSheet.class), DataSheet.class);
        addInputPort("showAll", "showAll", ValueType.of(Boolean.class), Boolean.class);
        addOutputPort("dataSheet", "dataSheet", ValueType.of(DataSheet.class), DataSheet.class);
        addOutputPort("allRows", "allRows", ValueType.of(Object.class), Object.class);
        addOutputPort("leadingRows", "leadingRows", ValueType.of(Object.class), Object.class);
        addOutputPort("headerRow", "headerRow", ValueType.of(String.class), String.class);
        addOutputPort("columnTypes", "columnTypes", ValueType.of(Object.class), Object.class);
        addOutputPort("dataRows", "dataRows", ValueType.of(Object.class), Object.class);
        addOutputPort("trailingRows", "trailingRows", ValueType.of(Object.class), Object.class);

    }

    @Override
    protected void initialize() {
    }

    @Override
    public Region getCustomization() {
        dataSheetViewer = new DataSheetViewer();
        return dataSheetViewer;
    }

    @Override
    protected void process() throws Exception {
        DataSheet dataSheet = (DataSheet) inputPorts.get(0).getData();
        Boolean showAll = (Boolean) inputPorts.get(1).getData();
        showAll = (showAll != null) ? showAll : false;

        if (dataSheet != null) {
            outputPorts.get(0).setData(dataSheet);
            outputPorts.get(1).setData(dataSheet.getAllRows());
            outputPorts.get(2).setData(dataSheet.getLeadingRows());
            outputPorts.get(3).setData(dataSheet.getHeaderRow());
            outputPorts.get(4).setData(dataSheet.getColumnTypes());
            outputPorts.get(5).setData(dataSheet.getDataRows());
            outputPorts.get(6).setData(dataSheet.getTrailingRows());
        }

        if (dataSheetViewer == null) {
            return;
        }
        dataSheetViewer.setDataSheet(dataSheet, showAll);
    }

    @Override
    protected void onRemoved() {
        if (dataSheetViewer == null) {
            return;
        }
        dataSheetViewer.remove();
    }

}
