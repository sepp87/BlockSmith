package blocksmith.domain.graph;

import blocksmith.TestApp;
import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockFactory;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.BlockLayout;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.ValueType;
import blocksmith.domain.value.ValueType.ListType;
import blocksmith.domain.value.ValueType.MapType;
import blocksmith.domain.value.ValueType.SimpleType;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author joost
 */
public class TypeEnvTest {

    private static BlockFactory factory;

    @BeforeAll
    public static void blockFactory() throws IOException {
        var app = new TestApp();
        factory = app.getBlockFactory();
    }

    @BeforeEach
    @AfterEach
    public void println() {
        System.out.println();
    }

    @Test
    public void testTypeOfGenericList_WhenStringRequired_ThenListOfString() {
        System.out.println("testTypeOf_WhenStringRequiredDownstream_ThenListOfString");

        // prepare test data
        var list = factory.create(BlockId.create(), "List.getFirst");
        var string = factory.create(BlockId.create(), "String.length");
        var connection = Connection.of(list.id(), "value", string.id(), "string");

        var graph = GraphFactory.createEmpty();
        graph = graph.withBlock(list);
        graph = graph.withBlock(string);
        graph = graph.withConnection(connection);
        var env = TypeEnv.of(graph);

        // perform test
        var target = PortRef.input(list.id(), "list");
        System.out.println("List<String> = " + env.typeOf(target));
        var resolved = (ListType) env.typeOf(target);
        var simple = (SimpleType) resolved.elementType();

        // prepare result
        var expected = String.class;
        var result = simple.raw();

        System.out.println("Expected: " + expected.getSimpleName() + ",  Result: " + result.getSimpleName());
        Assertions.assertTrue(expected == result);
    }

    @Test
    public void testTypeOfGenericList_WhenStringListFirst_ThenListOfString() {
        System.out.println("testTypeOfGenericList_WhenStringListFirst_ThenListOfString");

        // prepare test data
        var string = factory.create(BlockId.create(), "Input.string");
        var create = factory.create(BlockId.create(), "List.create");
        var first = factory.create(BlockId.create(), "List.getFirst");
        var stringToCreate = Connection.of(string.id(), "value", create.id(), "values#0");
        var createToFirst = Connection.of(create.id(), "value", first.id(), "list");

        var graph = GraphFactory.createEmpty();
        graph = graph.withBlock(string);
        graph = graph.withBlock(create);
        graph = graph.withBlock(first);
        graph = graph.withConnection(stringToCreate);
        graph = graph.withConnection(createToFirst);
        var env = TypeEnv.of(graph);

        // perform test
        var target = PortRef.output(first.id(), "value");
        System.out.println("String = " + env.typeOf(target));
        System.out.println("List<String> = " + env.typeOf(PortRef.output(create.id(), "value")));
        System.out.println("List<T> = " + env.typeOf(PortRef.input(first.id(), "list")));
        System.out.println("T = " + env.typeOf(PortRef.input(create.id(), "values#0")));
        System.out.println("T = " + env.typeOf(PortRef.input(create.id(), "values#1")));
        var resolved = (SimpleType) env.typeOf(target);

        // prepare result
        var expected = String.class;
        var result = resolved.raw();

        System.out.println("Expected: " + expected.getSimpleName() + ",  Result: " + result.getSimpleName());
        Assertions.assertTrue(expected == result);
    }

    public static Block concreteMapBlock() {
        return new Block(
                BlockId.create(),
                "test",
                List.of(),
                List.of(
                        Port.input(
                                "value",
                                0,
                                ValueType.of(ValueType.of(Integer.class), ValueType.of(String.class)),
                                false
                        )
                ),
                BlockLayout.createEmpty()
        );
    }

    @Test
    public void testTypeOfGenericMap_WhenIntegerAndStringRequired_ThenMapOfIntegerString() {
        System.out.println("testTypeOfGenericMap_WhenIntegerAndStringRequired_ThenMapOfIntegerString");

        // prepare test data
        var map = factory.create(BlockId.create(), "Map.create");
        var concrete = concreteMapBlock();
        var connection = Connection.of(map.id(), "value", concrete.id(), "value");

        var graph = GraphFactory.createEmpty();
        graph = graph.withBlock(map);
        graph = graph.withBlock(concrete);
        graph = graph.withConnection(connection);
        var env = TypeEnv.of(graph);

        // perform test
        var target = PortRef.output(map.id(), "value");
        System.out.println("Map<Integer, String> = " + env.typeOf(target));
        System.out.println("List<Integer> = " + env.typeOf(PortRef.input(map.id(), "keys")));
        System.out.println("List<String> = " + env.typeOf(PortRef.input(map.id(), "values")));
        var resolved = (MapType) env.typeOf(target);
        var key = (SimpleType) resolved.keyType();
        var element = (SimpleType) resolved.elementType();

        // prepare result
        var expectedKey = Integer.class;
        var resultKey = key.raw();
        var expectedElement = String.class;
        var resultElement = element.raw();

        System.out.println("Expected key: " + expectedKey.getSimpleName() + ",  Result key: " + resultKey.getSimpleName());
        Assertions.assertTrue(expectedKey == resultKey);
        System.out.println("Expected element: " + expectedElement.getSimpleName() + ",  Result element: " + resultElement.getSimpleName());
        Assertions.assertTrue(expectedElement == resultElement);

    }
    
    @Test
    public void testTypeOfList_WhenInputIntegerAndDouble_ThenListOfNumber() {
        System.out.println("testTypeOfList_WhenInputIntegerAndDouble_ThenListOfNumber");

        // prepare test data
        var intSlider = factory.create(BlockId.create(), "Input.integerSlider");
        var dblSlider = factory.create(BlockId.create(), "Input.doubleSlider");
        var create = factory.create(BlockId.create(), "List.create");
        var intToCreate = Connection.of(intSlider.id(), "value", create.id(), "values#0");
        var dblToCreate = Connection.of(dblSlider.id(), "value", create.id(), "values#1");

        var graph = GraphFactory.createEmpty();
        graph = graph.withBlock(intSlider);
        graph = graph.withBlock(dblSlider);
        graph = graph.withBlock(create);
        graph = graph.withConnection(intToCreate);
        graph = graph.withConnection(dblToCreate);
        var env = TypeEnv.of(graph);

        // perform test
        var target = PortRef.output(create.id(), "value");
        System.out.println("List<Number> = " + env.typeOf(target));
        System.out.println("T = " + env.typeOf(PortRef.input(create.id(), "values#0")));
        System.out.println("T = " + env.typeOf(PortRef.input(create.id(), "values#1")));
        var resolved = (ListType) env.typeOf(target);
        var simple = (SimpleType) resolved.elementType();

        // prepare result
        var expected = Number.class;
        var result = simple.raw();

        System.out.println("Expected: " + expected.getSimpleName() + ",  Result: " + result.getSimpleName());
        Assertions.assertTrue(expected == result);
    }
    

    
    
}
