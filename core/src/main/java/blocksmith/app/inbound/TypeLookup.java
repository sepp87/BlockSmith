package blocksmith.app.inbound;

import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.ValueType;

/**
 *
 * @author joostmeulenkamp
 */
public interface TypeLookup {

    ValueType typeOf(PortRef ref);
}
