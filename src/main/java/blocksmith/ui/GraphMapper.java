package blocksmith.ui;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.EditorMetadata;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.ValueType;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.group.Group;
import blocksmith.domain.value.Param;
import btscore.workspace.WorkspaceModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public final class GraphMapper {

    private GraphMapper() {

    }

    public static Graph toDomain(WorkspaceModel workspace) {
        var blocks = blocksToDomain(workspace);
        var connections = connectionsToDomain(workspace);
        var groups = groupsToDomain(workspace);

        return new Graph(null, blocks, connections, List.of());
    }

    private static List<Block> blocksToDomain(WorkspaceModel workspace) {
        var result = new ArrayList<Block>();

        for (var block : workspace.getBlockModels()) {

            if (block instanceof MethodBlockNew methodBlock) {
                var ports = portsToDomain(methodBlock);
                var params = paramsToDomain(methodBlock);
                var editorMetadata = new EditorMetadata(
                        methodBlock.nameProperty().get(),
                        methodBlock.layoutXProperty().get(),
                        methodBlock.layoutYProperty().get(),
                        methodBlock.resizableProperty().get() ? methodBlock.widthProperty().get() : null,
                        methodBlock.resizableProperty().get() ? methodBlock.heightProperty().get() : null
                );
                var domain = new Block(
                        BlockId.from(block.getId()),
                        methodBlock.getBlockDef().metadata().type(),
                        ports,
                        params,
                        editorMetadata
                );

                result.add(domain);
            }

        }
        return result;
    }

    private static List<Port> portsToDomain(MethodBlockNew block) {
        var result = new ArrayList<Port>();

        for (var input : block.getInputPorts()) {
            var valueId = input.nameProperty().get();
            var valueType = valueTypeFromPort(block, valueId);
            var domain = new Port(Port.Direction.INPUT, valueId, input.getData(), valueType);
            result.add(domain);
        }

        for (var output : block.getOutputPorts()) {
            var valueId = output.nameProperty().get();
            var valueType = valueTypeFromPort(block, valueId);
            var domain = new Port(Port.Direction.OUTPUT, valueId, output.getData(), valueType);
            result.add(domain);
        }

        return result;
    }

    private static List<Param> paramsToDomain(MethodBlockNew block) {
        var result = new ArrayList<Param>();
        for (var entry : block.getInputControls().entrySet()) {
            var control = entry.getValue();
            var valueId = entry.getKey();
            var value = control.isEditable() && control.getValue() != null ? control.getValue().toString() : null;
            var param = new Param(valueId, value, control.isEditable());
            result.add(param);
        }
        return result;
    }

    private static ValueType valueTypeFromPort(MethodBlockNew block, String valueId) {
        return block.getBlockDef().inputs().stream().filter(e -> e.valueId().equals(valueId)).findFirst().get().valueType();
    }

    private static List<Connection> connectionsToDomain(WorkspaceModel workspace) {
        var result = new ArrayList<Connection>();

        for (var connection : workspace.getConnectionModels()) {

            var from = connection.getStartPort();
            var to = connection.getEndPort();

            var fromRef = new PortRef(
                    BlockId.from(from.getBlock().getId()),
                    from.nameProperty().get()
            );
            var toRef = new PortRef(
                    BlockId.from(to.getBlock().getId()),
                    to.nameProperty().get()
            );

            var domain = new Connection(fromRef, toRef);
            result.add(domain);
        }

        return result;
    }
    
    private static List<Group> groupsToDomain(WorkspaceModel workspace) {
        var result = new ArrayList<Group>();
        for(var group : workspace.getBlockGroupModels()) {
            var label = group.nameProperty().get();
            var blocks = group.getBlocks().stream().map(b -> BlockId.from(b.getId())).toList();
            var domain = new Group(label, blocks);
            result.add(domain);
        }
        return result;
    }
 }
