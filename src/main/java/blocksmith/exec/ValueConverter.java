package blocksmith.exec;

import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.TypeCastUtils;
import blocksmith.domain.graph.ValueTypeResolver;
import blocksmith.domain.value.ValueType.ListType;
import blocksmith.domain.value.ValueType.SimpleType;
import blocksmith.domain.value.ValueType.VarType;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author joost
 */
public class ValueConverter {

    public static Object convert(Object sourceValue, PortRef source, PortRef target, Graph graph) {

        var sourceType = ValueTypeResolver.typeOf(graph, source);
        var targetType = ValueTypeResolver.typeOf(graph, target);

        var sourceInner = ValueTypeResolver.valueTypeWithin(sourceType);
        var targetInner = ValueTypeResolver.valueTypeWithin(targetType);

        if (sourceInner instanceof VarType || targetInner instanceof VarType) {
            return sourceValue;
        }

        var effectiveSimpleType = !(sourceValue instanceof List); // fragile e.g. breaks when introducing new collection types

        if (targetType instanceof ListType && sourceType instanceof SimpleType && effectiveSimpleType) {
            sourceValue = List.of(sourceValue);
        }

        var sourceSimple = (SimpleType) sourceInner; // fragile e.g. breaks when introducing new types
        var targetSimple = (SimpleType) targetInner;

        if (TypeCastUtils.contains(sourceSimple.raw()) && targetSimple.raw() == String.class) {
            return convert(sourceValue, Object.class, o -> o + "");
        }

        if (Path.class.isAssignableFrom(sourceSimple.raw()) && targetSimple.raw() == File.class) {
            return convert(sourceValue, Path.class, Path::toFile);
        }

        if (sourceSimple.raw() == File.class && Path.class.isAssignableFrom(targetSimple.raw())) {
            return convert(sourceValue, File.class, File::toPath);
        }

        return sourceValue;
    }

    private static <FROM, TO> Object convert(Object data, Class<FROM> from, Function<FROM, TO> converter) {
        if (data instanceof List) {
            var list = (List<FROM>) data;
            var newList = new ArrayList<>();
            for (var item : list) {
                var converted = convert(item, from, converter);
                newList.add(converted);
            }
            return newList;
        } else {
            return converter.apply(from.cast(data));
        }
    }
}
