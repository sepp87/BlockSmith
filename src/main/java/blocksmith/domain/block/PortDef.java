package blocksmith.domain.block;

import blocksmith.domain.block.Port.Direction;

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
