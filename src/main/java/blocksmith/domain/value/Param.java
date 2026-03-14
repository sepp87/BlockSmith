package blocksmith.domain.value;

import java.util.Objects;

/**
 *
 * @author joost
 */
public final class Param implements ValueSlot {

    private final String valueId;
    private final String value;
    private final ValueType valueType = ValueType.of(String.class);
    
    public Param(
            String valueId,
            String value
    ) {
        this.valueId = Objects.requireNonNull(valueId);
        this.value = value;
    }

    public String valueId() {
        return valueId;
    }

    public String value() {
        return value;
    }

    public ValueType valueType() {
        return valueType;
    }

    
    public Param withValue(String value) {
        return new Param(valueId, value);
    }
}
