package blocksmith.domain.graph;

import blocksmith.domain.block.Block;
import blocksmith.domain.connection.Connection;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.Port;
import blocksmith.domain.value.Port.Direction;
import static blocksmith.domain.value.Port.Direction.INPUT;
import static blocksmith.domain.value.Port.Direction.OUTPUT;
import blocksmith.domain.value.ValueType;
import blocksmith.domain.value.ValueType.ListType;
import blocksmith.domain.value.ValueType.MapType;
import blocksmith.domain.value.ValueType.SimpleType;
import blocksmith.domain.value.ValueType.VarType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author joost
 */
public class ValueTypeResolver {

    public static ValueType typeOf(Graph graph, PortRef ref) {
        var visited = new HashSet<PortRef>();
        return typeOf(graph, ref, visited);
    }

    private static ValueType typeOf(Graph graph, PortRef ref, Set<PortRef> visited) {
        visited.add(ref);

        // resolve locally
        var block = findBlock(graph, ref);
        var port = findPort(block, ref);

        var declared = port.valueType();
        var retrieved = varTypeWithin(declared);
        if (retrieved.isEmpty()) {
            return declared;
        }

        // resolve upstream
        var resolved = resolveVarTypeUpstream(graph, block, retrieved.get(), visited);
        if (resolved.isPresent()) {
            var withSimpleType = simpleTypeWithin(resolved.get());
            return substitute(declared, withSimpleType);
        }

        // resolve downstream
        resolved = resolveVarTypeDownstream(graph, block, retrieved.get(), visited);
        if (resolved.isPresent()) {
            var withSimpleType = simpleTypeWithin(resolved.get());
            return substitute(declared, withSimpleType);
        }

        return declared;
    }

    private static Block findBlock(Graph graph, PortRef ref) {
        return graph.block(ref.blockId()).orElseThrow(() -> new IllegalStateException("Block does NOT exist: " + ref.blockId()));
    }

    private static Port findPort(Block block, PortRef ref) {
        return block.port(ref.direction(), ref.valueId()).orElseThrow(() -> new IllegalStateException("Port does NOT exist: " + block.type() + " " + ref.blockId() + "." + ref.valueId()));
    }

    private static Optional<ValueType> resolveVarTypeUpstream(Graph graph, Block block, VarType varType, Set<PortRef> visited) {
        return resolveVarType(true, graph, block, varType, visited);
    }

    private static Optional<ValueType> resolveVarTypeDownstream(Graph graph, Block block, VarType varType, Set<PortRef> visited) {
        return resolveVarType(false, graph, block, varType, visited);
    }

    private static Optional<ValueType> resolveVarType(boolean upstream, Graph graph, Block block, VarType varType, Set<PortRef> visited) {

        var direction = upstream ? INPUT : OUTPUT;

        var connections = connectionsOfVarType(graph, block, varType, direction);
        if (connections.isEmpty()) {
            return Optional.empty();
        }

        var refs = filterUnvisited(upstream, connections, visited);
        var types = new ArrayList<ValueType>();

        for (var ref : refs) {
            var connectedBlock = findBlock(graph, ref);
            var port = findPort(connectedBlock, ref);

            var declared = port.valueType();
            var retrieved = varTypeWithin(declared);
            if (retrieved.isPresent()) {
                resolveVarType(upstream, graph, connectedBlock, retrieved.get(), visited).ifPresent(types::add);

            } else {
                types.add(declared);
            }
        }
        return consolidateValueType(types, upstream);
    }

    private static Optional<ValueType> consolidateValueType(Collection<ValueType> types, boolean upperBound) {
        if (types.isEmpty()) {
            return Optional.empty();
        }

        var simpleTypes = types.stream().map(ValueTypeResolver::simpleTypeWithin).toList();
        var bound = simpleTypes.getFirst();

        for (var candidate : simpleTypes) {
            if (upperBound) {
                bound = TypeCastUtils.isCastableTo(bound.raw(), candidate.raw()) ? candidate : bound;

            } else {
                bound = TypeCastUtils.isCastableTo(candidate.raw(), bound.raw()) ? candidate : bound;

            }
        }

        return Optional.of(bound);
    }

    private static List<PortRef> filterUnvisited(boolean upstream, Collection<Connection> connections, Set<PortRef> visited) {
        var unvisited = new ArrayList<PortRef>();
        for (var connection : connections) {
            var ref = upstream ? connection.from() : connection.to();
            if (visited.add(ref)) {
                unvisited.add(ref);
            }
        }
        return List.copyOf(unvisited);
    }

    private static List<Connection> connectionsOfVarType(Graph graph, Block block, VarType varType, Direction direction) {
        var ports = direction == INPUT ? block.inputPorts() : block.outputPorts();
        var varTyped = portsWithVarType(ports, varType);
        var refs = varTyped.stream().map(p -> PortRef.of(block.id(), p.direction(), p.valueId())).toList();
        var connections = refs.stream().flatMap(p -> graph.connectionsOf(p).stream()).toList();
        return connections;
    }

    private static List<Port> portsWithVarType(Collection<Port> ports, VarType varType) {
        var result = new ArrayList<Port>();
        for (var port : ports) {
            var candidate = varTypeWithin(port.valueType()).orElse(null);
            if (candidate == null) {
                continue;
            }
            if (candidate.equals(varType)) {
                result.add(port);
            }
        }
        return List.copyOf(result);
    }

    public static Optional<VarType> varTypeWithin(ValueType type) {
        var leafType = valueTypeWithin(type);
        if (leafType instanceof VarType varType) {
            return Optional.of(varType);
        }
        return Optional.empty();
    }

    public static ValueType valueTypeWithin(ValueType type) {
        return switch (type) {
            case SimpleType s ->
                type;
            case VarType v ->
                type;
            case ListType l ->
                valueTypeWithin(l.elementType());
            case MapType m ->
                type;
        };
    }

    private static ValueType substitute(ValueType varTyped, SimpleType withSimpleType) {
        if (varTyped instanceof ValueType.VarType) {
            return withSimpleType;
        }
        var listType = (ValueType.ListType) varTyped;
        return new ValueType.ListType(substitute(listType.elementType(), withSimpleType));
    }

    private static ValueType.SimpleType simpleTypeWithin(ValueType type) {
        if (type instanceof ValueType.SimpleType simpleType) {
            return simpleType;
        }
        var listType = (ValueType.ListType) type;
        return simpleTypeWithin(listType.elementType());
    }

    public static List<Port> boundOutputsOf(Block block, ValueType.VarType requested) {
        var result = new ArrayList<Port>();
        var outputs = block.outputPorts();
        for (var port : outputs) {
            var candidate = varTypeWithin(port.valueType()).orElse(null);
            if (candidate == null) {
                continue;
            }
            if (candidate.name().equals(requested.name())) {
                result.add(port);
            }
        }
        return List.copyOf(result);
    }

}
