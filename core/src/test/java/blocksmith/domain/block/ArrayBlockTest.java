
package blocksmith.domain.block;

import blocksmith.TestApp;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphFactory;
import blocksmith.domain.graph.GraphId;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author joost
 */
public class ArrayBlockTest {

    private static BlockFactory factory;

    private Graph graph;
    private Block input1;
    private Block input2;
    private ArrayBlock concat;

    @BeforeAll
    public static void blockFactory() throws IOException {
        var app = new TestApp();
        factory = app.getBlockFactory();
    }

    @BeforeEach
    public void prepareGraph() throws IOException {
        input1 = factory.create(BlockId.create(), "Input.string").withParamValue("string", "test1");
        input2 = factory.create(BlockId.create(), "Input.string").withParamValue("string", "test2");
        concat = (ArrayBlock) factory.create(BlockId.create(), "String.concat");

        graph = GraphFactory.create(
                GraphId.create(),
                List.of(
                        input1,
                        input2,
                        concat),
                List.of(),
                List.of());
    }

    @Test
    public void testInputPorts_WithConcat0_ThenSize1() {
        System.out.println("testInputPorts_WithConcat0_ThenSize1");

        var expected = 1;
        var result = concat.inputPorts().size();
        Assertions.assertEquals(expected, result, "");
        System.out.println("Expected: " + expected + ",  Result: " + result);
    }

    @Test
    public void testInputPorts_WithConcat1_ThenSize2() {
        System.out.println("testInputPorts_WithConcat1_ThenSize2");

        var connection = new Connection(
                PortRef.output(input1.id(), "value"),
                PortRef.input(concat.id(), "value#0")
        );

        graph = graph.withConnection(connection);
        concat = concat.withFittedElements(graph.connections());

        var expected = 2;
        var result = concat.inputPorts().size();
        Assertions.assertEquals(expected, result, "");
        System.out.println("Expected: " + expected + ",  Result: " + result);
    }
    
        @Test
    public void testInputPorts_WithConcat1_ThenSize2_reize() {
        System.out.println("testInputPorts_WithConcat1_ThenSize2_reize");

        var connection = new Connection(
                PortRef.output(input1.id(), "value"),
                PortRef.input(concat.id(), "value#0")
        );
        
        graph = graph.withConnection(connection).withoutConnection(connection).withConnection(connection);
        concat = concat.withFittedElements(graph.connections());

        var expected = 2;
        var result = concat.inputPorts().size();
        Assertions.assertEquals(expected, result, "");
        System.out.println("Expected: " + expected + ",  Result: " + result);
    }


    @Test
    public void testConnectedElements_WithConcat0_ThenSize0() {
        System.out.println("testConnectedElements_WithConcat0_ThenSize0");
        
        var expected = 0;
        var result = concat.connectedElements().size();
        Assertions.assertEquals(expected, result, "");
        System.out.println("Expected: " + expected + ",  Result: " + result);
    }

    @Test
    public void testConnectedElements_WithConcat1_ThenSize1() {
        System.out.println("testConnectedElements_WithConcat1_ThenSize1");

        var connection = new Connection(
                PortRef.output(input1.id(), "value"),
                PortRef.input(concat.id(), "value#0")
        );

        graph = graph.withConnection(connection);
        concat = concat.withFittedElements(graph.connections());

        var expected = 1;
        var result = concat.connectedElements().size();
        Assertions.assertEquals(expected, result, "");
        System.out.println("Expected: " + expected + ",  Result: " + result);
    }
}