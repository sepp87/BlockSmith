package blocksmith.domain.value;

import java.util.Objects;

/**
 *
 * @author joostmeulenkamp
 */
public final class Port implements ValueSlot {

    public enum Direction {
        INPUT,
        OUTPUT
    }

    private final Direction direction;
    private final String valueId;
    private final ValueType valueType;

    public Port(
            Direction direction,
            String valueId,
            ValueType valueType
    ) {
        this.direction = Objects.requireNonNull(direction);
        this.valueId = Objects.requireNonNull(valueId);
        this.valueType = Objects.requireNonNull(valueType);
    }

    public Direction direction() {
        return direction;
    }

    public String valueId() {
        return valueId;
    }

    public ValueType valueType() {
        return valueType;
    }

    public static Port input(String valueId, ValueType valueType) {
        return new Port(Direction.INPUT, valueId, valueType);
    }

    public static Port output(String valueId, ValueType valueType) {
        return new Port(Direction.OUTPUT, valueId, valueType);
    }

}
