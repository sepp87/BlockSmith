package blocksmith.infra.blockloader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joost
 */
public final class MethodLoaderUtils {

    private MethodLoaderUtils() {

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

    public static Map<String, Method> methodsByName(Class<?> clazz) {
        Map<String, Method> result = new HashMap<String, Method>();
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            var name = method.getName();
            result.put(name, method);
        }
        return result;
    }
    
    public static boolean isInstance(Method method) {
        return !Modifier.isStatic(method.getModifiers());
    }

    public static boolean isPublic(Method method) {
        return Modifier.isPublic(method.getModifiers());
    }

}
