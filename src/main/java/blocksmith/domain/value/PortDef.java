package blocksmith.domain.value;

import blocksmith.domain.value.Port.Direction;

/**
 *
 * @author joost
 */
public record PortDef(
        String valueId,
        int argIndex,
        String valueName,
        Direction direction,
        ValueType valueType,
        boolean isAutoConnectable,
        boolean display
        ) implements ValueDef{


    
}
