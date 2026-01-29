package blocksmith.domain.block;

/**
 *
 * @author joostmeulenkamp
 */
public record ParamDef(
        int argIndex,
        String name,
        Class<?> dataType // assumed only string, date, path, integer, decimal, ...
        ) {

}
