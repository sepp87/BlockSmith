package blocksmith.domain.block;

import java.util.Objects;

/**
 *
 * @author joostmeulenkamp
 */
public final class Port {

    public enum Direction {
        INPUT,
        OUTPUT
    }

    private final Direction direction;
    private final int index;
    private final Class<?> dataType;
//    private final ValueType valueType;

    public Port(
            Direction direction,
            int index,
            Class<?> dataType
//            , ValueType valueType
    ) {
        this.direction = Objects.requireNonNull(direction);
        this.index = Objects.requireNonNull(index);
        this.dataType = Objects.requireNonNull(dataType);
//        this.valueType = Objects.requireNonNull(valueType);
    }

    public Direction direction() {
        return direction;
    }

    public int index() {
        return index;
    }

    public Class<?> dataType() {
        return dataType;
    }

}
