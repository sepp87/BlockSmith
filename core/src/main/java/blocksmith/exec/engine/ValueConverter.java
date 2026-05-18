package blocksmith.exec.engine;

import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.TypeCastUtils;
import blocksmith.domain.value.ValueType.ListType;
import blocksmith.domain.value.ValueType.MapType;
import blocksmith.domain.value.ValueType.SimpleType;
import blocksmith.domain.value.ValueType.VarType;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import blocksmith.app.inbound.TypeLookup;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author joost
 */
public class ValueConverter {

    private final TypeLookup valueTypeLookup;

    public ValueConverter(TypeLookup valueTypeLookup) {
        this.valueTypeLookup = valueTypeLookup;
    }

    public Object convert(Object sourceValue, PortRef source, PortRef target) {
        var sourceType = valueTypeLookup.typeOf(source);
        var targetType = valueTypeLookup.typeOf(target);

        if (sourceType instanceof MapType sourceMap && targetType instanceof MapType targetMap) {
            return convertMap(sourceValue, sourceMap, targetMap);
        }

        var sourceInner = sourceType.valueTypeWithin();
        var targetInner = targetType.valueTypeWithin();

        if (sourceInner instanceof VarType || targetInner instanceof VarType) {
            return sourceValue;
        }

        var sourceSimple = (SimpleType) sourceInner;
        var targetSimple = (SimpleType) targetInner;

        if (targetType instanceof ListType && sourceType instanceof SimpleType && !(sourceValue instanceof List)) {
            sourceValue = List.of(sourceValue);
        }

        return convertValue(sourceValue, sourceSimple, targetSimple);
    }

    static Object convertMap(Object sourceValue, MapType sourceMap, MapType targetMap) {
        if (!(sourceMap.keyType() instanceof SimpleType sourceKey)
                || !(sourceMap.elementType() instanceof SimpleType sourceElement)
                || !(targetMap.keyType() instanceof SimpleType targetKey)
                || !(targetMap.elementType() instanceof SimpleType targetElement)) {
            return sourceValue; // unresolved VarTypes — no conversion possible
        }

        var source = (Map<?, ?>) sourceValue;
        var result = new LinkedHashMap<>();
        for (var entry : source.entrySet()) {
            var convertedKey = convertValue(entry.getKey(), sourceKey, targetKey);
            var convertedVal = convertValue(entry.getValue(), sourceElement, targetElement);
            result.put(convertedKey, convertedVal);
        }
        return result;
    }

    static Object convertValue(Object value, SimpleType sourceSimple, SimpleType targetSimple) {
        if (TypeCastUtils.contains(sourceSimple.raw()) && targetSimple.raw() == String.class) {
            return convert(value, Object.class, o -> o + "");
        }
        if (Path.class.isAssignableFrom(sourceSimple.raw()) && targetSimple.raw() == File.class) {
            return convert(value, Path.class, Path::toFile);
        }
        if (sourceSimple.raw() == File.class && Path.class.isAssignableFrom(targetSimple.raw())) {
            return convert(value, File.class, File::toPath);
        }

        return value;
    }

//    public Object convert(Object sourceValue, PortRef source, PortRef target) {
//
//        var sourceType = valueTypeLookup.typeOf(source);
//        var targetType = valueTypeLookup.typeOf(target);
//
//        if (sourceType instanceof MapType fromMap && targetType instanceof MapType toMap) {
//            // TODO - no conversion for maps             
//            return sourceValue;
//        }
//
//        var sourceInner = sourceType.valueTypeWithin();
//        var targetInner = targetType.valueTypeWithin();
//
//        if (sourceInner instanceof VarType || targetInner instanceof VarType) {
//            return sourceValue;
//        }
//
//        var effectiveSimpleType = !(sourceValue instanceof List); // fragile e.g. breaks when introducing new collection types
//
//        if (targetType instanceof ListType && sourceType instanceof SimpleType && effectiveSimpleType) {
//            sourceValue = List.of(sourceValue);
//        }
//
//        var sourceSimple = (SimpleType) sourceInner; // fragile e.g. breaks when introducing new types
//        var targetSimple = (SimpleType) targetInner;
//
//        if (TypeCastUtils.contains(sourceSimple.raw()) && targetSimple.raw() == String.class) {
//            return convert(sourceValue, Object.class, o -> o + "");
//        }
//
//        if (Path.class.isAssignableFrom(sourceSimple.raw()) && targetSimple.raw() == File.class) {
//            return convert(sourceValue, Path.class, Path::toFile);
//        }
//
//        if (sourceSimple.raw() == File.class && Path.class.isAssignableFrom(targetSimple.raw())) {
//            return convert(sourceValue, File.class, File::toPath);
//        }
//
//        return sourceValue;
//    }
    static <FROM, TO> Object convert(Object data, Class<FROM> from, Function<FROM, TO> converter) {
        if (data instanceof List) {
            var list = (List<FROM>) data;
            var result = new ArrayList<>();
            for (var item : list) {
                var converted = convert(item, from, converter);
                result.add(converted);
            }
            return result;
        }
        return converter.apply(from.cast(data));

    }

}
