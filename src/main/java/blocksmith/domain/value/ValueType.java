package blocksmith.domain.value;

/**
 *
 * @author joost
 */
public sealed interface ValueType {

    public record SimpleType(Class<?> raw) implements ValueType {

    }

    public record ListType(ValueType elementType) implements ValueType {

    }

    public record VarType(String name) implements ValueType {

    }

    public static Class<?> toDataType(ValueType valueType) {
        var current = valueType;
        while (current instanceof ListType listType) {
            current = listType.elementType;
        }
        if (current instanceof SimpleType simpleType) {
            return simpleType.raw;
        }
        return Object.class;
    }

    public static SimpleType of(Class<?> raw) {
        return new SimpleType(raw);
    }

    public static ListType of(ValueType elementType) {
        return new ListType(elementType);
    }

    public static VarType of(String name) {
        return new VarType(name);
    }
}
