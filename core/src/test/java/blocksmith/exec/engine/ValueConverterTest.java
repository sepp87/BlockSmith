package blocksmith.exec.engine;

import blocksmith.domain.value.ValueType;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author joost
 */
public class ValueConverterTest {

    @Test
    public void testConvertValue_WhenPathProvidedAndFileRequired_ThenConvertToFile() {
        System.out.println("testConvertValue_WhenPathProvidedAndFileRequired_ThenConvertToFile");

        var value = Path.of("/example/path");
        var source = ValueType.of(Path.class);
        var target = ValueType.of(File.class);

        var result = ValueConverter.convertValue(value, source, target);
        Assertions.assertTrue(result instanceof File, "Expected: File.class,  Result: " + result.getClass().getSimpleName());
    }

    @Test
    public void testConvertMap_WhenPathProvidedAndFileRequired_ThenConvertToFile() {
        System.out.println("testConvertMap_WhenPathProvidedAndFileRequired_ThenConvertToFile");

        var key = "someId";
        var value = Map.of(
                key,
                Path.of("/example/path")
        );

        var source = ValueType.of(
                ValueType.of(String.class),
                ValueType.of(Path.class)
        );
        var target = ValueType.of(
                ValueType.of(String.class),
                ValueType.of(File.class)
        );

        var result = (Map<?, ?>) ValueConverter.convertMap(value, source, target);
        Assertions.assertTrue(result.containsKey(key));

        var element = result.get(key);
        Assertions.assertTrue(element instanceof File, "Expected: File.class,  Result: " + element.getClass().getSimpleName());

    }
}
