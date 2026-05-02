package blocksmith.ui.projection;

import blocksmith.app.logging.GraphLogFmt;
import blocksmith.domain.block.ArrayBlock;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.ValueTypeResolver;
import blocksmith.domain.graph.ValueTypeResolver2;
import blocksmith.domain.value.Port;
import static blocksmith.domain.value.Port.Direction.INPUT;
import blocksmith.exec.engine.ExecutionState;
import blocksmith.ui.graph.block.BlockModelFactory;
import blocksmith.ui.graph.block.MethodBlockNew;
import blocksmith.ui.graph.port.PortModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class BlockProjectionAssembler {

    private static final Logger LOGGER = Logger.getLogger(BlockProjectionAssembler.class.getName());

    private final BlockModelFactory blockFactory;
    private final ExecutionState runtime;

    public BlockProjectionAssembler(BlockModelFactory blockFactory, ExecutionState runtime) {
        this.blockFactory = blockFactory;
        this.runtime = runtime;
    }

    public Map<BlockId, MethodBlockNew> create(Collection<Block> blocks, Graph graph) {
        var result = new HashMap<BlockId, MethodBlockNew>();
        for (var block : blocks) {

            var blockFx = blockFactory.create(block);

            updatePorts(blockFx, block, graph);
            updateBlock(blockFx, block, graph);
            updateInputControls(blockFx, block, graph);

            var id = block.id();
            var blockState = runtime.stateOf(id);
            blockFx.updateFrom(blockState);

            result.put(block.id(), blockFx);
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

    public void updateBlock(MethodBlockNew blockFx, Block block, Graph graph) {
        blockFx.updateLayoutFrom(block.layout());
        updateInputControls(blockFx, block, graph);

        if (block instanceof ArrayBlock) {

            var fxPorts = blockFx.getInputPorts().stream().map(PortModel::valueId).toList();
            var domainPorts = block.inputPorts().stream().map(Port::valueId).toList();

            var removed = new ArrayList<String>(fxPorts);
            var added = new ArrayList<String>();

            for (var id : domainPorts) {
                var isPresent = removed.remove(id);
                if (!isPresent) {
                    added.add(id);
                }
            }

            for (var id : removed) {
                LOGGER.log(Level.FINEST, GraphLogFmt.block(block.id()) + "." + id + " array element port removed");
                blockFx.removeInputPort(id);
            }

            for (var id : added) {
                LOGGER.log(Level.FINEST, GraphLogFmt.block(block.id()) + "." + id + " array element port added");
                var valueType = block.port(INPUT, id).get().valueType();
                blockFx.addInputPort(id, id, valueType);
            }
        }

    }

    public void updateInputControl(MethodBlockNew projection, PortRef ref, Graph graph) {

        var block = graph.block(ref.blockId()).orElseThrow();
        var valueId = ref.valueId();

        var param = block.param(valueId).orElse(null);
        if (param == null) {
            return;
        }

        var controls = projection.getInputControls();
        var control = controls.get(valueId);
        control.setValue(param.value());
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
