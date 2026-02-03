package blocksmith.domain.block;

import blocksmith.domain.block.Port.Direction;

/**
 *
 * @author joost
 */
public record PortDef(
        int argIndex,
        String name,
        Direction direction,
//        Class<?> dataType,
        ValueType valueType,
        boolean isAutoConnectable
//        , // TODO refactor > move elsewhere
//        boolean isList, // TODO refactor > move elsewhere
//        boolean dataTypeIsGeneric
        ) implements ValueDef{ // TODO refactor > move elsewhere

}
