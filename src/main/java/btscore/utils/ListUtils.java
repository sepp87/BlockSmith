package btscore.utils;

import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public class ListUtils {

    public static int getListCount(Object... parameters) {
        int result = 0;
        int i = 0;
        for (Object p : parameters) {
            if (p != null) {
                System.out.println("parameter " + i + " is " + p.getClass().getSimpleName());
            } else {
                System.out.println("parameter " + i + " is " + null);
            }
            
            if (ListUtils.isList(p)) {
                result++;
            }
            i++;
        }
        return result;
    }

    public static int getShortestListSize(Object... parameters) {
        List<?> first = getFirstList(parameters);
        int result = -1;
        if (first == null) {
            return result;
        }
        result = first.size();
        for (Object p : parameters) {
            if (ListUtils.isList(p)) {
                List<?> list = (List<?>) p;
                result = list.size() < result ? list.size() : result;
            }
        }
        return result;
    }

    public static List<?> getFirstList(Object... parameters) {
        for (Object p : parameters) {
            if (ListUtils.isList(p)) {
                return (List<?>) p;
            }
        }
        return null;
    }

    public static boolean isList(Object o) {
        if (o == null) {
            return false;
        } else {
            return List.class.isAssignableFrom(o.getClass());
        }
    }
}
