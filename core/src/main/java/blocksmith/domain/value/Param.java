package blocksmith.domain.value;

import java.util.Objects;

/**
 *
 * @author joost
 */
public final class Param implements ValueSlot {

    private final String valueId;
    private final int argIndex;
    private final String value;
    private final ValueType valueType = ValueType.of(String.class);
    private final ParamInput input;

    public Param(
            String valueId,
            int argIndex,
            String value,
            ParamInput input
    ) {
        this.valueId = Objects.requireNonNull(valueId);
        this.argIndex = argIndex;
        this.value = value;
        this.input = input;
    }

    public String valueId() {
        return valueId;
    }

    public int argIndex() {
        return argIndex;
    }

    public String value() {
        return value;
    }

    public ValueType valueType() {
        return valueType;
    }

    public ParamInput input() {
        return input;
    }

    public Param withValue(String value) {
        return new Param(valueId, argIndex, value, input);
    }

    public Param withInput(ParamInput input) {
        return new Param(valueId, argIndex, value, input);
    }
}
