package btslib.autoconnect;

import blocksmith.domain.value.ValueType;
import blocksmith.ui.icons.FontAwesomeSolid;
import java.net.http.HttpClient;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import btsxml.BlockTag;
import blocksmith.ui.graph.block.BlockModel;
import blocksmith.ui.graph.block.BlockView;
import blocksmith.infra.blockloader.annotations.Block;

/**
 *
 * @author JoostMeulenkamp
 */
@Block(
        type = "Autoconnect.Transmitter",
        category = "General",
        description = "A template block for further customization",
        tags = {"template", "dummy", "example"})
public class TransmitterBlock extends BlockModel {

    public TransmitterBlock() {
        this.labelProperty().set("Transmitter");
        addOutputPort("Client","Client", ValueType.of(HttpClient.class), HttpClient.class).autoConnectableProperty().set(true);
        initialize();
    }

    @Override
    protected final void initialize() {
        // Event handlers, change listeners and bindings
    }

    @Override
    public Region getCustomization() {
        Label label = BlockView.getAwesomeIcon(FontAwesomeSolid.PAPER_PLANE);
        return label;
    }

    /**
     * process function is called whenever new data is incoming
     */
    @Override
    public void process() {
    }


    @Override
    protected void onRemoved() {
        // Remove event handlers, change listeners and bindings
    }
}
