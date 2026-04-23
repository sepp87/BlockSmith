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
import java.util.stream.Stream;

/**
 *
 * @author joost
 */
public class ValueTypeResolver1 {

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
        var retrieved = varTypesWithin(declared);
        if (retrieved.isEmpty()) {
            return declared;
        }

        // resolve upstream first to fit resolved to actual type
        var result = declared;
        for (var target : retrieved) {
            var resolved = resolveVarTypeUpstream(graph, block, target, visited);
            if (resolved.isEmpty()) {
                // resolve downstream second to fit port's connectabilty
                resolved = resolveVarTypeDownstream(graph, block, target, visited);
            }

            if (resolved.isPresent()) {
                var withSimpleType = simpleTypeWithin(resolved.get());
                result = substitute(result, target, withSimpleType);
            }
        }
        return result;
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
            var retrieved = varTypesWithin(declared);
            if (!retrieved.isEmpty()) {
                for (var item : retrieved) {
                    resolveVarType(upstream, graph, connectedBlock, item, visited).ifPresent(types::add);
                }

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

        var simpleTypes = types.stream().map(ValueTypeResolver1::simpleTypeWithin).toList();
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

    private static Collection<Port> portsWithVarType(Collection<Port> ports, VarType requested) {
        var result = new HashSet<Port>();
        for (var port : ports) {
            var varTypes = varTypesWithin(port.valueType());
            for (var candidate : varTypes) {
                if (candidate.equals(requested)) {
                    result.add(port);
                }
            }
        }
        return Set.copyOf(result);
    }

    public static Collection<Port> boundOutputsOf(Block block, VarType requested) {
        var result = new HashSet<Port>();
        var outputs = block.outputPorts();
        for (var port : outputs) {
            var varTypes = varTypesWithin(port.valueType());
            for (var candidate : varTypes) {
                if (candidate.name().equals(requested.name())) {
                    result.add(port);
                }
            }
        }
        return Set.copyOf(result);
    }

    public static List<VarType> varTypesWithin(ValueType type) {
        var result = new ArrayList<VarType>();
        var innerTypes = valueTypesWithin(type);
        for (var leaf : innerTypes) {
            if (leaf instanceof VarType varType) {
                result.add(varType);
            }
        }
        return List.copyOf(result);
    }

    public static List<ValueType> valueTypesWithin(ValueType type) {
        return switch (type) {
            case SimpleType s ->
                List.of(type);
            case VarType v ->
                List.of(type);
            case ListType l ->
                valueTypesWithin(l.elementType());
            case MapType m -> {
                var keys = valueTypesWithin(m.keyType());
                var elements = valueTypesWithin(m.elementType());
                yield Stream.concat(keys.stream(), elements.stream()).toList();
            }
        };
    }

    private static ValueType substitute(ValueType varTyped, VarType target, SimpleType withSimpleType) {
        return switch (varTyped) {
            case SimpleType s ->
                varTyped;
            case VarType v -> {
                if (v.equals(target)) {
                    yield withSimpleType;
                }
                yield varTyped;
            }
            case ListType l ->
                new ListType(substitute(l.elementType(), target, withSimpleType));
            case MapType m -> {
                var keyType = substitute(m.keyType(), target, withSimpleType);
                var elementType = substitute(m.elementType(), target, withSimpleType);
                yield new MapType(keyType, elementType);
            }
        };
    }

    private static ValueType.SimpleType simpleTypeWithin(ValueType type) {
        if (type instanceof ValueType.SimpleType simpleType) {
            return simpleType;
        }
        var listType = (ValueType.ListType) type;
        return simpleTypeWithin(listType.elementType());
    }

}
