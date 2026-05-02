package blocksmith.domain.graph;

import blocksmith.domain.block.Block;
import blocksmith.domain.block.BlockId;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.value.Port;
import static blocksmith.domain.value.Port.Direction.INPUT;
import static blocksmith.domain.value.Port.Direction.OUTPUT;
import blocksmith.domain.value.ValueType;
import blocksmith.domain.value.ValueType.ListType;
import blocksmith.domain.value.ValueType.MapType;
import blocksmith.domain.value.ValueType.SimpleType;
import blocksmith.domain.value.ValueType.VarType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author joost
 */
public class TypeEnv {

    private final Map<PortRef, ValueType> resolved;

    private TypeEnv(Map<PortRef, ValueType> resolved) {
        this.resolved = Map.copyOf(resolved);
    }

    public static TypeEnv of(Graph graph) {
        var cache = new HashMap<PortRef, ValueType>();
        var visiting = new HashSet<BlockId>();
        for (var block : graph.blocks()) {
            resolveBlock(graph, block, cache, visiting);
        }
        return new TypeEnv(cache);
    }

    public ValueType typeOf(PortRef ref) {
        return resolved.getOrDefault(ref, ValueType.of(Object.class));
    }

    // --- resolution ---
    private static void resolveBlock(Graph graph, Block block, Map<PortRef, ValueType> cache, Set<BlockId> visiting) {
        if (!visiting.add(block.id())) {
            return;  // cycle guard only
        }
        var upstreamEnv = buildEnv(graph, block, cache, visiting, INPUT);
        var downstreamEnv = buildEnv(graph, block, cache, visiting, OUTPUT);

        var outputEnv = new HashMap<>(downstreamEnv);
        outputEnv.putAll(upstreamEnv);

        for (var port : block.ports()) {
            var ref = PortRef.of(block.id(), port.direction(), port.valueId());
            cache.put(ref, applyEnv(port.valueType(), outputEnv));
        }

        visiting.remove(block.id());  // allow re-resolution by outer loop with more context
    }

    private static Map<VarType, SimpleType> buildEnv(Graph graph, Block block, Map<PortRef, ValueType> cache, Set<BlockId> visiting, Port.Direction side) {
        var env = new HashMap<VarType, SimpleType>();
        var ports = side == INPUT ? block.inputPorts() : block.outputPorts();

        for (var port : ports) {
            var ref = PortRef.of(block.id(), port.direction(), port.valueId());
            for (var connection : graph.connectionsOf(ref)) {
                var connectedRef = side == INPUT ? connection.from() : connection.to();
                var connectedType = resolvedTypeOf(graph, connectedRef, cache, visiting);
                collectBindings(port.valueType(), connectedType, env);
            }
        }
        return env;
    }

    private static ValueType resolvedTypeOf(Graph graph, PortRef ref, Map<PortRef, ValueType> cache, Set<BlockId> visiting) {
        if (cache.containsKey(ref)) {
            return cache.get(ref);
        }

        graph.block(ref.blockId()).ifPresent(b -> resolveBlock(graph, b, cache, visiting));

        if (cache.containsKey(ref)) {
            return cache.get(ref);
        }

        // cycle or unresolvable — fall back to declared
        return graph.block(ref.blockId())
                .flatMap(b -> b.port(ref.direction(), ref.valueId()))
                .map(Port::valueType)
                .orElse(ValueType.of(Object.class));
    }

    // --- binding collection ---
    private static void collectBindings(ValueType template, ValueType mirror, Map<VarType, SimpleType> env) {
        switch (template) {
            case VarType v -> {
                if (mirror instanceof SimpleType s) {
                    env.put(v, s);
                }
            }
            case ListType l -> {
                if (mirror instanceof ListType r) {
                    collectBindings(l.elementType(), r.elementType(), env);
                }
            }
            case MapType m -> {
                if (mirror instanceof MapType r) {
                    collectBindings(m.keyType(), r.keyType(), env);
                    collectBindings(m.elementType(), r.elementType(), env);
                }
            }
            case SimpleType s -> {
            }
        }
    }

    // --- substitution ---
    private static ValueType applyEnv(ValueType type, Map<VarType, SimpleType> env) {
        return switch (type) {
            case SimpleType s ->
                type;
            case VarType v ->
                env.containsKey(v) ? env.get(v) : type;
            case ListType l ->
                new ListType(applyEnv(l.elementType(), env));
            case MapType m ->
                new MapType(applyEnv(m.keyType(), env), applyEnv(m.elementType(), env));
        };
    }
}
