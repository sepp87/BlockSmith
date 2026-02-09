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
    private final Object value;
    private final ValueType valueType;

    public Port(
            Direction direction,
            String valueId,
            Object value,
            ValueType valueType
    ) {
        this.direction = Objects.requireNonNull(direction);
        this.valueId = Objects.requireNonNull(valueId);
        this.value = value;
        this.valueType = Objects.requireNonNull(valueType);
    }

    public Direction direction() {
        return direction;
    }

    public String valueId() {
        return valueId;
    }

    public Object value() {
        return value;
    }

    public ValueType valueType() {
        return valueType;
    }

}
