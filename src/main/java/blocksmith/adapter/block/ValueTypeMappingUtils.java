package blocksmith.adapter.block;

import blocksmith.domain.value.ValueType;
import blocksmith.domain.value.ValueType.ListType;
import blocksmith.domain.value.ValueType.SimpleType;
import blocksmith.domain.value.ValueType.VarType;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

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

            if (raw instanceof Class<?> rawClass && List.class.isAssignableFrom(rawClass)) {
                Type arg = pt.getActualTypeArguments()[0];

                // List<T>
                if (arg instanceof TypeVariable<?> tv) {
                    return new ListType(new VarType(tv.getName()));
                }

                // List<?>
                if (arg instanceof WildcardType wc) {
                    return new ListType(new SimpleType(Object.class));
                }

                // List<Concrete>
                return new ListType(fromType(arg));
            }
        }

        // List (raw type)
        if (type instanceof Class<?> cls
                && List.class.isAssignableFrom(cls)) {
            return new ListType(new SimpleType(Object.class));
        }

        if (type instanceof Class<?> cls) {
            return new SimpleType(cls);
        }

        if (type instanceof WildcardType) {
            return new SimpleType(Object.class);
        }

        if (type instanceof TypeVariable<?> tv) {
            return new VarType(tv.getName());
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
    }

}
