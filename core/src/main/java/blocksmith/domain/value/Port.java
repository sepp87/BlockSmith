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
    private final boolean isElement;

    public Port(
            Direction direction,
            String valueId,
            int argIndex,
            ValueType valueType,
            boolean isElement
    ) {
        this.direction = Objects.requireNonNull(direction);
        this.valueId = Objects.requireNonNull(valueId);
        this.argIndex = argIndex;
        this.valueType = Objects.requireNonNull(valueType);
        this.isElement = isElement;
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
    
    public boolean isElement() {
        return isElement;
    }

    public static Port input(String valueId, int argIndex, ValueType valueType, boolean isElement) {
        return new Port(Direction.INPUT, valueId, argIndex, valueType, isElement);
    }

    public static Port output(String valueId, int argIndex, ValueType valueType) {
        return new Port(Direction.OUTPUT, valueId, argIndex, valueType, false);
    }
    
    public Port copy (String valueId) {
        return new Port(direction, valueId, argIndex, valueType, isElement);
    }

}
