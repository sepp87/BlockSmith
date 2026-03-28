package blocksmith.domain.graph;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.BlockLayout;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.ValueType;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author joost
 */
public class ConnectionPolicyTest {

    public static Block blockWithPort(Port port) {
        return blockWithPorts(List.of(port));
    }

    public static Block blockWithPorts(Collection<Port> ports) {
        return new Block(
                BlockId.create(),
                "test",
                List.of(),
                ports,
                BlockLayout.createEmpty()
        );
    }

    @Test
    public void testIsConnectable_WhenVarTypeIsStringDownstream_ThenNotConnectableToDoubleInput() {
        System.out.println("testIsConnectable_WhenVarTypeIsStringDownstream_ThenNotConnectableToDoubleInput");

        var outputVarType = blockWithPort(Port.output("value", 0, ValueType.of("T")));

        var inputString = blockWithPort(Port.input("value", 0, ValueType.of(String.class)));
        var inputDouble = blockWithPort(Port.input("value", 1, ValueType.of(Double.class)));

        var varTypeToString = new Connection(
                PortRef.output(outputVarType.id(), "value"),
                PortRef.input(inputString.id(), "value")
        );

        var graph = GraphFactory.create(GraphId.create(),
                List.of(outputVarType, inputString, inputDouble),
                List.of(varTypeToString),
                List.of());

        var isConnectable = ConnectionPolicy.isConnectable(graph,
                PortRef.output(outputVarType.id(), "value"),
                PortRef.input(inputDouble.id(), "value")
        );

        Assertions.assertFalse(isConnectable);
    }

    @Test
    public void testIsConnectable_WhenVarTypeIsStringUpstream_ThenNotConnectableToDoubleInput() {
        System.out.println("testIsConnectable_WhenVarTypeIsStringUpstream_ThenNotConnectableToDoubleInput");

        var outputString = blockWithPort(Port.output("value", 0, ValueType.of(String.class)));

        var varTyped = blockWithPorts(List.of(
                Port.input("value", 0, ValueType.of("T")),
                Port.output("value", 0, ValueType.of("T"))
        ));

        var inputDouble = blockWithPort(Port.input("value", 0, ValueType.of(Double.class)));

        var stringToVarType = new Connection(
                PortRef.output(outputString.id(), "value"),
                PortRef.input(varTyped.id(), "value")
        );

        var graph = GraphFactory.create(GraphId.create(),
                List.of(outputString, varTyped, inputDouble),
                List.of(stringToVarType),
                List.of());

        var isConnectable = ConnectionPolicy.isConnectable(graph,
                PortRef.output(varTyped.id(), "value"),
                PortRef.input(inputDouble.id(), "value")
        );

        Assertions.assertFalse(isConnectable);
    }

}
