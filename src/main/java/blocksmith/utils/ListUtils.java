package blocksmith.utils;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 *
 * @author joostmeulenkamp
 */
public class ListUtils {

    private ListUtils() {

    }

    public static int getListCount(Object... args) {
        int result = 0;
        for (Object arg : args) {
            if (ListUtils.isList(arg)) {
                result++;
            }
        }
        return result;
    }

    public static OptionalInt getShortestListSize(Object... args) {
        List<?> first = getFirstList(args).orElse(null);
        if (first == null) {
            OptionalInt.empty();
        }
        var result = first.size();
        for (Object arg : args) {
            if (ListUtils.isList(arg)) {
                List<?> list = (List<?>) arg;
                result = Math.min(list.size(), result);
            }
        }
        return OptionalInt.of(result);
    }

    public static Optional<List<?>> getFirstList(Object... args) {
        for (Object arg : args) {
            if (ListUtils.isList(arg)) {
                return Optional.of((List<?>) arg);
            }
        }
        return Optional.empty();
    }

    public static boolean isList(Object o) {
        return o instanceof List<?>;
    }
}
