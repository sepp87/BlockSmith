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
    private final boolean active;

    public Param(
            String valueId,
            String value,
            boolean active
    ) {
        this.valueId = Objects.requireNonNull(valueId);
        this.value = value;
        this.active = active;
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

    public boolean isActive() {
        return active;
    }

    public Param activate() {
        return new Param(valueId, value,  true);
    }

    public Param deactivate() {
        return new Param(valueId, value, false);
    }
    
    public Param withValue(String value) {
        return new Param(valueId, value,  active);
    }
}
