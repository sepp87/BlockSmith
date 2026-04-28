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
public class ValueTypeResolverTest {

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

        // perform test
        var target = PortRef.input(list.id(), "list");
        var resolved = (ListType) ValueTypeResolver.typeOf(graph, target);
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
        var stringToCreate = Connection.of(string.id(), "value", create.id(), "item#0");
        var createToFirst = Connection.of(create.id(), "value", first.id(), "list");
        
        var graph = GraphFactory.createEmpty();
        graph = graph.withBlock(string);
        graph = graph.withBlock(create);
        graph = graph.withBlock(first);
        graph = graph.withConnection(stringToCreate);
        graph = graph.withConnection(createToFirst);

        // perform test
        var target = PortRef.output(first.id(), "value");
        var resolved = (SimpleType) ValueTypeResolver.typeOf(graph, target);

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

        // perform test
        var target = PortRef.output(map.id(), "value");
        var resolved = (MapType) ValueTypeResolver2.typeOf(graph, target);
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

}
