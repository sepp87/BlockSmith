package blocksmith;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.List;

/**
 *
 * @author joost
 */
public class Drafts {


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

    public static <T> void test(Integer foo, List<T> test) {

    }

    public static void test2(List<Integer> test) {

    }
}
