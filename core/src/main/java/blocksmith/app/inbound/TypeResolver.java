package blocksmith.app.inbound;

import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.ValueType;

/**
 *
 * @author joostmeulenkamp
 */
public interface TypeResolver {

    ValueType typeOf(PortRef ref);
}
