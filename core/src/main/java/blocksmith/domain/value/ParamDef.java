package blocksmith.domain.value;

/**
 *
 * @author joostmeulenkamp
 */
public record ParamDef(
        String valueId,
        int argIndex,
        String valueName,
        ValueType valueType,
        ParamInput input) implements ValueDef {

}
