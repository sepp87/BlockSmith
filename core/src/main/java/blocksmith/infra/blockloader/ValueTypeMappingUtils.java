package blocksmith.infra.blockloader;

import blocksmith.domain.value.ValueType;
import blocksmith.domain.value.ValueType.ListType;
import blocksmith.domain.value.ValueType.MapType;
import blocksmith.domain.value.ValueType.SimpleType;
import blocksmith.domain.value.ValueType.VarType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joost
 */
public class ValueTypeMappingUtils {

    private ValueTypeMappingUtils() {

    }

    public static ValueType fromMethodParameter(Parameter parameter) {
        Type type = parameter.getParameterizedType();
        return fromType(type);
    }

    public static ValueType fromType(Type type) {

        if (type instanceof ParameterizedType pt) {
            Type raw = pt.getRawType();

            if (raw instanceof Class<?> rawClass) {

                // List<>
                if (List.class.isAssignableFrom(rawClass)) {
                    return genericListType(pt);
                }

                // Map<,>
                if (Map.class.isAssignableFrom(rawClass)) {
                    return genericMapType(pt);
                }
            }

            // Simple<Class>
            if (raw == Class.class) {
                return new SimpleType(Class.class);
            }
        }

        // List (raw type)
        if (type instanceof Class<?> cls
                && List.class.isAssignableFrom(cls)) {
            return new ListType(new SimpleType(Object.class));
        }

        // Map (raw type)
        if (type instanceof Class<?> cls
                && Map.class.isAssignableFrom(cls)) {
            return new MapType(new SimpleType(Object.class), new SimpleType(Object.class));
        }

        // concrete
        if (type instanceof Class<?> cls) {
            
            // concrete[]
            if (cls.isArray()) {
                return fromType(cls.getComponentType());
            }

            // concrete
            return new SimpleType(cls);
        }

        // TBD TODO check if non-occurring-case
        if (type instanceof WildcardType) {
            System.out.println("WildcardType " + new SimpleType(Object.class));
            return new SimpleType(Object.class);
        }

        // T
        if (type instanceof TypeVariable<?> tv) {
            return new VarType(tv.getName());
        }

        // T[]
        if (type instanceof GenericArrayType gat) {
            return new VarType(gat.getGenericComponentType().getTypeName());
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    private static ListType genericListType(ParameterizedType pt) {
        Type arg = pt.getActualTypeArguments()[0];
        return new ListType(generic(arg));
    }

    private static MapType genericMapType(ParameterizedType pt) {
        Type arg0 = pt.getActualTypeArguments()[0];
        Type arg1 = pt.getActualTypeArguments()[1];

        var keyType = generic(arg0);
        var valType = generic(arg1);

        return new MapType(keyType, valType);
    }

    private static ValueType generic(Type arg) {
        // ...<T>
        if (arg instanceof TypeVariable<?> tv) {
            return new VarType(tv.getName());
        }

        // ...<?>
        if (arg instanceof WildcardType wc) {
            return new SimpleType(Object.class);
        }

        // ...<Concrete>
        return fromType(arg);
    }

}
