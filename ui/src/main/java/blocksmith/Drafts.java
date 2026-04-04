package blocksmith;

import blocksmith.domain.value.AutoConnectable;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.PortDef;
import blocksmith.infra.blockloader.MethodLoaderUtils;
import blocksmith.infra.blockloader.ValueTypeMappingUtils;
import blocksmith.infra.blockloader.annotations.Display;
import blocksmith.infra.blockloader.annotations.Value;
import blocksmith.lib.HelloWorldMethods;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.List;

/**
 *
 * @author joost
 */
public class Drafts {

    public static void outputDefs() throws ReflectiveOperationException {

        var methods = MethodLoaderUtils.getStaticMethodsFromClasses(List.of(HelloWorldMethods.class));
        for (var method : methods) {

            var name = method.getName();
            System.out.println(name);
            if (name.equalsIgnoreCase("hellorecord")) {
                outputDefFromReturnType(method);
            }
        }
    }

    public static PortDef outputDefFromReturnType(Method method) throws ReflectiveOperationException {
        Class<?> returnType = method.getReturnType();

        if (returnType.isRecord()) {
            int argIndex = 0;
            for (var component : returnType.getRecordComponents()) {

                boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(component.getType());

                System.out.println(component.getName());
                Value value = component.getAnnotatedType().getAnnotation(Value.class);
                boolean hasAnnotation = value != null;

                var valueName = component.getName();
                var valueType = ValueTypeMappingUtils.fromType(component.getGenericType());
                var valueId = hasAnnotation && !value.id().isEmpty() ? value.id() : valueName;

                var display = returnType.getAnnotation(Display.class) != null;
                var portDef = new PortDef(
                        valueId,
                        argIndex,
                        valueName,
                        Port.Direction.OUTPUT,
                        valueType,
                        isAutoConnectable,
                        display
                );
                argIndex++;
            }
        }

        boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(returnType);

        try {
            Value value = method.getAnnotatedReturnType().getAnnotation(Value.class);
            boolean hasAnnotation = value != null;

            var valueType = ValueTypeMappingUtils.fromType(method.getGenericReturnType());
            var valueId = hasAnnotation && !value.id().isEmpty() ? value.id() : "value";

            var portDef = new PortDef(
                    valueId,
                    -1,
                    valueId,
                    Port.Direction.OUTPUT,
                    valueType,
                    isAutoConnectable,
                    false
            );

            return portDef;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "OUTPUT of block definition unknown. " + e.getMessage());
        }

    }

    public static void getGenericTypeOfMethodParam() {
        var methods = Drafts.class.getMethods();

        for (var method : methods) {
            for (Parameter p : method.getParameters()) {

                if (p.getParameterizedType() instanceof ParameterizedType type) {
                    System.out.println(type.getActualTypeArguments()[0]);
                    System.out.println(type.getActualTypeArguments()[0].getClass());
                    System.out.println(type.getActualTypeArguments()[0].getTypeName());
                    if (type.getActualTypeArguments()[0] instanceof TypeVariable<?> tv) {
                        System.out.println(tv.getBounds().length);
                        System.out.println(tv.getBounds()[0]);

                    }

                }

                if (List.class.isAssignableFrom(p.getType())) {

                } else {

                }
            }
        }
    }

}
