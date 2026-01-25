package blocksmith.domain.block;

import blocksmith.domain.block.Port.Direction;

/**
 *
 * @author joost
 */
public record PortDef(
        String name,
        Direction direction,
        Class<?> dataType,
        boolean dataTypeIsGeneric) {

}
