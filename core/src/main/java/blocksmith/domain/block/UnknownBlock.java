package blocksmith.domain.block;

import blocksmith.domain.value.Param;
import blocksmith.domain.value.Port;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author joost
 */
public final class UnknownBlock extends Block {

    public UnknownBlock(BlockId id, String type, Collection<Param> params, Collection<Port> ports, BlockLayout layout) {
        super(id, type, params, ports, layout);
    }

    public static UnknownBlock create(BlockId id, String type) {
        return new UnknownBlock(id, type, List.of(), List.of(), null);
    }

    @Override
    protected Block copy(Collection<Param> params, Collection<Port> ports, BlockLayout layout) {
        return new UnknownBlock(id(), type(), params, ports, layout);
    }

    @Override
    public Block duplicate(BlockId id) {
        return new UnknownBlock(
                id,
                type(),
                params(),
                ports(),
                layout()
        );
    }
}
