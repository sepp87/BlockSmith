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
    private final int argIndex;
    private final ValueType valueType;

    public Port(
            Direction direction,
            String valueId,
            int argIndex,
            ValueType valueType
    ) {
        this.direction = Objects.requireNonNull(direction);
        this.valueId = Objects.requireNonNull(valueId);
        this.argIndex = argIndex;
        this.valueType = Objects.requireNonNull(valueType);
    }

    public Direction direction() {
        return direction;
    }

    public String valueId() {
        return valueId;
    }

    public int argIndex() {
        return argIndex;
    }

    public ValueType valueType() {
        return valueType;
    }

    public static Port input(String valueId, int argIndex, ValueType valueType) {
        return new Port(Direction.INPUT, valueId, argIndex, valueType);
    }

    public static Port output(String valueId, int argIndex, ValueType valueType) {
        return new Port(Direction.OUTPUT, valueId, argIndex, valueType);
    }

}
