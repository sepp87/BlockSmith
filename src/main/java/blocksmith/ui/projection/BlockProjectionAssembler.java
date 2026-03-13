package blocksmith.ui.projection;

import blocksmith.app.logging.GraphLogFmt;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.ParamStatusResolver;
import blocksmith.domain.graph.ValueTypeResolver;
import blocksmith.domain.graph.ValueTypeResolver;
import static blocksmith.domain.value.Port.Direction.INPUT;
import blocksmith.ui.BlockModelFactory;
import blocksmith.ui.graph.block.MethodBlockNew;
import blocksmith.ui.graph.port.PortModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author joost
 */
public class BlockProjectionAssembler {

    private final BlockModelFactory blockFactory;

    public BlockProjectionAssembler(BlockModelFactory blockFactory) {
        this.blockFactory = blockFactory;
    }

    public Map<BlockId, MethodBlockNew> create(Collection<Block> blocks, Graph graph) {
        var result = new HashMap<BlockId, MethodBlockNew>();
        for (var block : blocks) {
            var projection = blockFactory.create(block.type(), block.id().toString());

            projection.setActive(true);
            updatePorts(projection, block, graph);
            updateBlock(projection, block, graph);
            updateInputControls(projection, block, graph);

            result.put(block.id(), projection);
        }
        return Map.copyOf(result);
    }

    private void updateInputControls(MethodBlockNew projection, Block block, Graph graph) {
        for (var param : block.params()) {
            var ref = PortRef.input(block.id(), param.valueId());
            updateInputControl(projection, ref, graph);
        }
    }

    private void updatePorts(MethodBlockNew projection, Block block, Graph graph) {
        for (var port : block.ports()) {
            var varType = ValueTypeResolver.varTypeWithin(port.valueType());
            if (varType.isPresent()) {
                var ref = PortRef.of(block.id(), port.direction(), port.valueId());
                updatePort(projection, ref, graph);
            }
        }
    }

    public void updateBlock(MethodBlockNew projection, Block state, Graph graph) {
        projection.updateFrom(state);
        projection.updateLayoutFrom(state.layout());
//        projection.updateInputControlsFrom(state);

    }

    public void updateInputControl(MethodBlockNew projection, PortRef ref, Graph graph) {

        var block = graph.block(ref.blockId()).orElseThrow();
        var valueId = ref.valueId();

        var param = block.param(valueId).orElse(null);
        if (param == null) {
            return;
        }
        var isActive = ParamStatusResolver.isActive(graph, block.id(), valueId);

        var controls = projection.getInputControls();
        var control = controls.get(valueId);

        if (isActive) {
            findPort(projection.getInputPorts(), valueId)
                    .ifPresent(p -> {
                        control.unbindValueProperty();
                    });
            control.setValue(param.value());
            control.setEditable(true); // setEditable after setValue, so InputControl.valueChangedByUser is NOT triggered

        } else {
            control.setEditable(false); // setEditable before setValue, so InputControl.valueChangedByUser is NOT triggered
            findPort(projection.getInputPorts(), valueId)
                    .ifPresent(p -> {
                        control.bindValuePropertyTo(p.dataProperty());
                    });
        }

    }

    public void updatePort(MethodBlockNew projection, PortRef ref, Graph graph) {
        var type = ValueTypeResolver.typeOf(graph, ref);
        var ports = ref.direction() == INPUT ? projection.getInputPorts() : projection.getOutputPorts();

        findPort(ports, ref.valueId()).ifPresentOrElse(
                p -> p.updateValueType(type),
                () -> new IllegalStateException("Port NOT found")
        );
    }

    private Optional<PortModel> findPort(Collection<PortModel> ports, String valueId) {
        for (var port : ports) {
            if (port.valueId().equals(valueId)) {
                return Optional.of(port);
            }
        }
        return Optional.empty();
    }
}
