package blocksmith.domain.graph;

import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.ValueType;
import blocksmith.domain.value.ValueType.MapType;
import blocksmith.domain.value.ValueType.SimpleType;
import blocksmith.domain.value.ValueType.VarType;

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
        var typeEnv = TypeEnv.of(toEvaluate);

        var fromType = typeEnv.typeOf(from);
        var toType = typeEnv.typeOf(to);

        if (fromType instanceof MapType fromMap && toType instanceof MapType toMap) {
            var fromKey = fromMap.keyType().valueTypeWithin();
            var toKey = toMap.keyType().valueTypeWithin();

            var fromElement = fromMap.elementType().valueTypeWithin();
            var toElement = toMap.elementType().valueTypeWithin();

            return isCompatible(fromKey, toKey) && isCompatible(fromElement, toElement);
        }

        if (fromType instanceof MapType || toType instanceof MapType) {
            return false;
        }

//        var initialFromLeafType = graph.port(from).valueType().valueTypeWithin();
        var fromLeafType = fromType.valueTypeWithin();
        var toLeafType = toType.valueTypeWithin();

//        if (initialFromLeafType instanceof VarType
//                && fromLeafType instanceof SimpleType
//                && toLeafType instanceof SimpleType) {
//
//            
//        }

        return isCompatible(fromLeafType, toLeafType);

    }

    private static boolean isCompatible(ValueType from, ValueType to) {

        if (from instanceof VarType || to instanceof VarType) {
            return true;
        }

        if (from instanceof SimpleType simpleFrom && to instanceof SimpleType simpleTo) {
            return TypeCastUtils.isCastableTo(simpleFrom.raw(), simpleTo.raw());
        }

        return false;
    }
}
