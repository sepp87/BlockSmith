package blocksmith.domain.graph;

import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.ValueType;

/**
 *
 * @author joost
 */
public class ConnectionPolicy {

    public static boolean isConnectable(Graph graph, PortRef from, PortRef to) {

        if(from.direction().equals(to.direction())) {
            return false;
        }
        
        if(from.blockId().equals(to.blockId())) {
            return false;
        }
        
        var fromType = ValueTypeResolver.typeOf(graph, from);
        var toType = ValueTypeResolver.typeOf(graph, to);

        var fromLeafType = ValueTypeResolver.valueTypeWithin(fromType);
        var toLeafType = ValueTypeResolver.valueTypeWithin(toType);

        return isCompatible(fromLeafType, toLeafType);

    }

    private static boolean isCompatible(ValueType from, ValueType to) {

        if (from instanceof ValueType.VarType || to instanceof ValueType.VarType) {
            return true;
        }

        if (from instanceof ValueType.SimpleType simpleFrom && to instanceof ValueType.SimpleType simpleTo) {
            return TypeCastUtils.isCastableTo(simpleFrom.raw(), simpleTo.raw());
        }

        return false;
    }
}
