package blocksmith.domain.graph;

import blocksmith.domain.connection.PortRef;
import blocksmith.domain.block.Block;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.ValueType;
import blocksmith.domain.value.ValueType.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author joost
 */
public final class ValueTypeResolver {

    private ValueTypeResolver() {}
    
    public static ValueType typeOf(Graph graph, PortRef ref) {
        var visited = new HashSet<PortRef>();
        return typeOf(graph, ref, visited);
    }

    private static ValueType typeOf(Graph graph, PortRef ref, Set<PortRef> visited) {
        visited.add(ref);

        var blockId = ref.blockId();
        var direction = ref.direction();
        var valueId = ref.valueId();

        var block = graph.block(blockId).orElseThrow(() -> new IllegalStateException("Block does NOT exist: " + blockId));
        var port = block.port(direction, valueId).orElseThrow(() -> new IllegalStateException("Port does NOT exist: " + blockId + "." + valueId));

        var declared = port.valueType();

        if (!visited.add(ref)) {
            return declared;
        }

        var retrieved = varTypeWithin(declared);
        if (retrieved.isEmpty()) {
            return declared;
        }

        var input = port.direction() == Port.Direction.INPUT ? port : boundInputOf(block, retrieved.get());
        var inputRef = new PortRef(block.id(), input.direction(), input.valueId());
        var connection = graph.connectionsOf(inputRef); // zero or one if connected or NOT
        if (connection.isEmpty()) {
            return declared;
        }

        // look upstream to resolve the type
        var upstreamRef = connection.getFirst().from();
        if (visited.contains(upstreamRef)) {
            return declared;
        }

        var upstreamType = typeOf(graph, upstreamRef, visited);
        var unresolved = varTypeWithin(upstreamType).isPresent();
        if (unresolved) {
            return declared;
        }

        var upstreamSimpleType = simpleTypeWithin(upstreamType);
        var resolved = substitute(declared, upstreamSimpleType);
        return resolved;

    }

    private static ValueType substitute(ValueType varTyped, SimpleType withSimpleType) {
        if (varTyped instanceof VarType) {
            return withSimpleType;
        }
        var listType = (ListType) varTyped;
        return new ListType(substitute(listType.elementType(), withSimpleType));
    }

    private static SimpleType simpleTypeWithin(ValueType type) {
        if (type instanceof SimpleType simpleType) {
            return simpleType;
        }
        var listType = (ListType) type;
        return simpleTypeWithin(listType.elementType());
    }

    private static Port boundInputOf(Block block, VarType requested) {
        var inputs = block.inputPorts();
        for (var input : inputs) {
            var candidate = varTypeWithin(input.valueType()).orElse(null);
            if (candidate == null) {
                continue;
            }
            if (candidate.name().equals(requested.name())) {
                return input;
            }
        }
        throw new IllegalStateException("VarType NOT found amongst input ports: " + requested.name());
    }

    private static Optional<VarType> varTypeWithin(ValueType type) {
        if (type instanceof SimpleType) {
            return Optional.empty();
        }
        if (type instanceof ListType listType) {
            return varTypeWithin(listType.elementType());
        }
        var varType = (VarType) type;
        return Optional.of(varType);
    }

}

//        var upstreamType = typeOf(graph, upstreamRef, visited);
//        var upstreamVarType = varTypeWithin(upstreamType);
//        if (upstreamVarType.isEmpty()) {
//            var upstreamSimpleType = simpleTypeWithin(upstreamType);
//            var resolved = substitute(declared, upstreamSimpleType);
//            return resolved;
//        }
//
//        // type could NOT be resolved, return original declared value type
//        return declared;
//
//    }
