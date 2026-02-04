package blocksmith.domain.block;

/**
 *
 * @author joostmeulenkamp
 */
public record ParamDef(
        String valueId,
        int argIndex,
        String valueName,
        Class<?> dataType, // assumed only string, date, path, integer, decimal, ...
        ValueType valueType,
        ParamInput input
        ) implements ValueDef{

}
