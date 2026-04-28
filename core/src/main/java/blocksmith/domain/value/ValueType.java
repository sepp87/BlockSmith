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
    
    public record MapType(ValueType keyType, ValueType elementType) implements ValueType {
        
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
    
    public static MapType of(ValueType keyType, ValueType elementType) {
        return new MapType(keyType, elementType);
    }

}
