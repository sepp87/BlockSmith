package blocksmith.domain.graph;

import blocksmith.domain.block.Block;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.Port;
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
public class ValueTypeResolver2 {

    public static ValueType typeOf(Graph graph, PortRef ref) {
        var visited = new HashSet<PortRef>();
        return typeOf(graph, ref, visited);
    }

    private static ValueType typeOf(Graph graph, PortRef ref, Set<PortRef> visited) {
        visited.add(ref);
        var block = findBlock(graph, ref);
        var port = findPort(block, ref);
        var declared = port.valueType();

        if (declared instanceof MapType m) {
            return typeOfPair(graph, block, m, visited);
        }
        return typeOfSingle(graph, block, declared, visited);
    }

    private static ValueType typeOfPair(Graph graph, Block block, MapType mapType, Set<PortRef> visited) {
        var resolvedKey = typeOfSingle(graph, block, mapType.keyType(), new HashSet<>(visited));
        var resolvedElement = typeOfSingle(graph, block, mapType.elementType(), new HashSet<>(visited));
        return new MapType(resolvedKey, resolvedElement);
    }

    private static ValueType typeOfSingle(Graph graph, Block block, ValueType declared, Set<PortRef> visited) {
        var varType = varTypeWithin(declared);
        if (varType.isEmpty()) {
            return declared;
        }

        var resolved = resolveVarTypeUpstream(graph, block, varType.get(), visited);
        if (resolved.isPresent()) {
            return substitute(declared, varType.get(), simpleTypeWithin(resolved.get()));
        }

        resolved = resolveVarTypeDownstream(graph, block, varType.get(), visited);
        if (resolved.isPresent()) {
            return substitute(declared, varType.get(), simpleTypeWithin(resolved.get()));
        }

        return declared;
    }

    private static Optional<ValueType> resolveVarTypeUpstream(Graph graph, Block block, VarType varType, Set<PortRef> visited) {
        return resolveVarType(true, graph, block, varType, visited);
    }

    private static Optional<ValueType> resolveVarTypeDownstream(Graph graph, Block block, VarType varType, Set<PortRef> visited) {
        return resolveVarType(false, graph, block, varType, visited);
    }

    private static Optional<ValueType> resolveVarType(boolean upstream, Graph graph, Block block, VarType varType, Set<PortRef> visited) {
        var direction = upstream ? INPUT : OUTPUT;
        var originalPorts = portsWithVarType(direction == INPUT ? block.inputPorts() : block.outputPorts(), varType);
        var types = new ArrayList<ValueType>();

        for (var originalPort : originalPorts) {
            var originalRef = PortRef.of(block.id(), originalPort.direction(), originalPort.valueId());
            var connections = graph.connectionsOf(originalRef);

            for (var connection : connections) {
                var ref = upstream ? connection.from() : connection.to();
                if (!visited.add(ref)) {
                    continue;
                }

                var connectedBlock = findBlock(graph, ref);
                var connectedPort = findPort(connectedBlock, ref);
                var declared = connectedPort.valueType();

                if (declared instanceof MapType m) {
                    var resolved = typeOfPair(graph, connectedBlock, m, new HashSet<>(visited));
                    slotOf(originalPort.valueType(), varType, resolved).ifPresent(types::add);
                } else {
                    var retrieved = varTypeWithin(declared);
                    if (retrieved.isPresent()) {
                        resolveVarType(upstream, graph, connectedBlock, retrieved.get(), visited).ifPresent(types::add);
                    } else {
                        types.add(declared);
                    }
                }
            }
        }
        return consolidateValueType(types, upstream);
    }

    private static Optional<ValueType> slotOf(ValueType template, VarType target, ValueType mirror) {
        return switch (template) {
            case VarType v ->
                v.equals(target) ? Optional.of(mirror) : Optional.empty();
            case ListType l ->
                mirror instanceof ListType r
                ? slotOf(l.elementType(), target, r.elementType())
                : Optional.empty();
            case MapType m -> {
                if (!(mirror instanceof MapType r)) {
                    yield Optional.empty();
                }
                var fromKey = slotOf(m.keyType(), target, r.keyType());
                yield fromKey.isPresent() ? fromKey : slotOf(m.elementType(), target, r.elementType());
            }
            case SimpleType s ->
                Optional.empty();
        };
    }

    private static Optional<ValueType> consolidateValueType(Collection<ValueType> types, boolean upperBound) {
        if (types.isEmpty()) {
            return Optional.empty();
        }
        var simpleTypes = types.stream().map(ValueTypeResolver2::simpleTypeWithin).toList();
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

    private static List<Port> portsWithVarType(Collection<Port> ports, VarType varType) {
        var result = new ArrayList<Port>();
        for (var port : ports) {
            if (port.valueType() instanceof MapType m) {
                if (containsVarType(m.keyType(), varType) || containsVarType(m.elementType(), varType)) {
                    result.add(port);
                }
            } else {
                varTypeWithin(port.valueType()).filter(varType::equals).ifPresent(v -> result.add(port));
            }
        }
        return List.copyOf(result);
    }

    private static boolean containsVarType(ValueType type, VarType target) {
        return switch (type) {
            case VarType v ->
                v.equals(target);
            case ListType l ->
                containsVarType(l.elementType(), target);
            case MapType m ->
                containsVarType(m.keyType(), target) || containsVarType(m.elementType(), target);
            case SimpleType s ->
                false;
        };
    }

    public static Optional<VarType> varTypeWithin(ValueType type) {
        return switch (type) {
            case SimpleType s ->
                Optional.empty();
            case VarType v ->
                Optional.of(v);
            case ListType l ->
                varTypeWithin(l.elementType());
            case MapType m ->
                throw new IllegalStateException("MapType must be decomposed before calling varTypeWithin");
        };
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
                throw new IllegalStateException("MapType must be decomposed before calling valueTypeWithin");
        };
    }

    private static ValueType substitute(ValueType type, VarType target, SimpleType replacement) {
        return switch (type) {
            case VarType v ->
                v.equals(target) ? replacement : type;
            case ListType l ->
                new ListType(substitute(l.elementType(), target, replacement));
            case SimpleType s ->
                type;
            case MapType m ->
                throw new IllegalStateException("MapType must be decomposed before calling substitute");
        };
    }

    private static SimpleType simpleTypeWithin(ValueType type) {
        return switch (type) {
            case SimpleType s ->
                s;
            case ListType l ->
                simpleTypeWithin(l.elementType());
            case VarType v ->
                throw new IllegalStateException("Unresolved VarType: " + v.name());
            case MapType m ->
                throw new IllegalStateException("MapType must be decomposed before calling simpleTypeWithin");
        };
    }

    public static Collection<Port> boundOutputsOf(Block block, VarType requested) {
        var result = new HashSet<Port>();
        for (var port : block.outputPorts()) {
            if (port.valueType() instanceof MapType m) {
                if (containsVarType(m.keyType(), requested) || containsVarType(m.elementType(), requested)) {
                    result.add(port);
                }
            } else {
                varTypeWithin(port.valueType())
                        .filter(v -> v.name().equals(requested.name()))
                        .ifPresent(v -> result.add(port));
            }
        }
        return Set.copyOf(result);
    }

    private static Block findBlock(Graph graph, PortRef ref) {
        return graph.block(ref.blockId()).orElseThrow(() -> new IllegalStateException("Block does NOT exist: " + ref.blockId()));
    }

    private static Port findPort(Block block, PortRef ref) {
        return block.port(ref.direction(), ref.valueId()).orElseThrow(() -> new IllegalStateException("Port does NOT exist: " + block.type() + " " + ref.blockId() + "." + ref.valueId()));
    }
}
