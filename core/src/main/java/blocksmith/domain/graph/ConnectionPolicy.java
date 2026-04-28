package blocksmith.domain.graph;

import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.ValueType;
import blocksmith.domain.value.ValueType.MapType;

/**
 *
 * @author joost
 */
public class ConnectionPolicy {

    public static boolean isConnectable(Graph graph, PortRef from, PortRef to) {

        if (from.direction().equals(to.direction())) {
            return false;
        }

        if (from.blockId().equals(to.blockId())) {
            return false;
        }

        var toEvaluate = graph.incomingConnection(to)
                .map(graph::withoutConnection)
                .orElse(graph);

        var fromType = ValueTypeResolver.typeOf(toEvaluate, from);
        var toType = ValueTypeResolver.typeOf(toEvaluate, to);

//        if (fromType instanceof MapType fromMap && toType instanceof MapType toMap) {
//            var fromKey = ValueTypeResolver2.valueTypeWithin(fromMap.keyType());
//            var toKey = ValueTypeResolver2.valueTypeWithin(toMap.keyType());
//            
//            var fromElement = ValueTypeResolver2.valueTypeWithin(fromMap.elementType());
//            var toElement = ValueTypeResolver2.valueTypeWithin(toMap.elementType());
//            
//            return isCompatible(fromKey, toKey) && isCompatible(fromElement, toElement);
//        }
//
//        if (fromType instanceof MapType || toType instanceof MapType) {
//            return false;
//        }

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
