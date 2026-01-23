
package blocksmith.adapter.block;

import btscore.graph.block.BlockMetadata;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public final class ReflectionUtils {
    
    private ReflectionUtils() {
        
    }
    
    public static List<Method> getStaticMethodsFromClasses(Collection<Class<?>> classes) {
        List<Method> result = new ArrayList<>();
        for (Class<?> clazz : classes) {
            List<Method> methods = getStaticMethodsFromClass(clazz);
            result.addAll(methods);
        }
        return result;
    }

    private static List<Method> getStaticMethodsFromClass(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (Modifier.isStatic(m.getModifiers())) {
                result.add(m);
            }
        }
        return result;
    }

    public static List<Method> filterMethodsByAnnotation(Collection<Method> methods, Class<? extends Annotation> clazz) {
        List<Method> result = new ArrayList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(clazz)) {
                result.add(method);
            }
        }
        return result;
    }

}
