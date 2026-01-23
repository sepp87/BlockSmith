package blocksmith.domain.block;

import blocksmith.domain.block.Port.Direction;

/**
 *
 * @author joostmeulenkamp
 */
public record PortDef(
        Direction direction,
        Class<?> dataType) {

}
