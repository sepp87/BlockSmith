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
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 * Resolves the concrete ValueType of every port within the graph in one single
 * pass. Input ports are defined by connected downstream types. If there is none
 * declared downstream it stay VarType. Outputs are defined by upstream types.
 * If there are none defined upstream, then downstream types (when available)
 * are used as fallback. In case of multiple connected types, the applicable
 * type is derived as described by the following examples:
 *
 * <ul>
 * <li>Input ports - a list with 2 items, a double and an integer, will output a
 * list of numbers. Try out block "List.create"</li>
 * <li>Output ports - a VarTyped block connected downstream to 2 ports, of which
 * one requires a number and the other an integer, will require a list of
 * integers as input. Try out block "List.getFirst" </li>
 * </ul>
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
            return;
        }

        // input ports: lower bound from downstream connections (what this block's outputs connect to)
        var inputEnv = buildEnv(graph, block, cache, visiting, OUTPUT);

        // output ports: lower bound from upstream input connections, downstream as fallback
        var upstreamEnv = buildEnv(graph, block, cache, visiting, INPUT);
        var outputEnv = new HashMap<>(inputEnv); // reuse downstream env as fallback
        outputEnv.putAll(upstreamEnv); // upstream lower bound takes priority

        for (var port : block.ports()) {
            var ref = PortRef.of(block.id(), port.direction(), port.valueId());
            var env = port.direction() == OUTPUT ? outputEnv : inputEnv;
            cache.put(ref, applyEnv(port.valueType(), env));
        }

        visiting.remove(block.id()); // allow re-resolution by outer loop with more context
    }

    private static Map<VarType, SimpleType> buildEnv(
            Graph graph,
            Block block,
            Map<PortRef, ValueType> cache,
            Set<BlockId> visiting,
            Port.Direction side) {

        var candidates = new HashMap<VarType, List<SimpleType>>();
        var ports = side == INPUT ? block.inputPorts() : block.outputPorts();

        for (var port : ports) {
            var ref = PortRef.of(block.id(), port.direction(), port.valueId());
            for (var connection : graph.connectionsOf(ref)) {
                var connectedRef = side == INPUT ? connection.from() : connection.to();
                var connectedType = resolvedTypeOf(graph, connectedRef, cache, visiting);
                collectCandidates(port.valueType(), connectedType, candidates);
            }
        }

        var env = new HashMap<VarType, SimpleType>();
        for (var entry : candidates.entrySet()) {
            consolidate(entry.getValue()).ifPresent(s -> env.put(entry.getKey(), s));
        }
        return env;
    }

    private static ValueType resolvedTypeOf(
            Graph graph, 
            PortRef ref,
            Map<PortRef, ValueType> cache, 
            Set<BlockId> visiting) {
        
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

    // --- candidate collection ---
    private static void collectCandidates(ValueType template, ValueType mirror, Map<VarType, List<SimpleType>> candidates) {
        switch (template) {
            case VarType v -> {
                if (mirror instanceof SimpleType s) {
                    candidates.computeIfAbsent(v, k -> new ArrayList<>()).add(s);
                }
            }
            case ListType l -> {
                if (mirror instanceof ListType r) {
                    collectCandidates(l.elementType(), r.elementType(), candidates);
                }
            }
            case MapType m -> {
                if (mirror instanceof MapType r) {
                    collectCandidates(m.keyType(), r.keyType(), candidates);
                    collectCandidates(m.elementType(), r.elementType(), candidates);
                }
            }
            case SimpleType s -> {
            }
        }
    }

    // --- consolidation ---
    private static Optional<SimpleType> consolidate(List<SimpleType> types) {
        if (types.isEmpty()) {
            return Optional.empty();
        }
        var bound = types.getFirst().raw();
        for (var candidate : types) {
            var c = candidate.raw();
            var boxedBound = box(bound);
            var boxedC = box(c);
            if (TypeCastUtils.isCastableTo(boxedC, boxedBound)) {
                bound = c; // c is more specific
            } else if (!TypeCastUtils.isCastableTo(boxedBound, boxedC)) {
                bound = leastUpperBound(bound, c); // siblings — fall back to LCA
            }
            // else bound is already more specific, keep it
        }
        return Optional.of(new SimpleType(bound));
    }

    private static Class<?> leastUpperBound(Class<?> a, Class<?> b) {
        a = box(a);
        b = box(b);
        if (a.isAssignableFrom(b)) {
            return a;
        }
        if (b.isAssignableFrom(a)) {
            return b;
        }
        // walk boxed class hierarchy for common ancestor
        var ancestor = a.getSuperclass();
        while (ancestor != null && ancestor != Object.class) {
            if (ancestor.isAssignableFrom(b)) {
                return ancestor;
            }
            ancestor = ancestor.getSuperclass();
        }
        return Object.class;
    }

    private static Class<?> box(Class<?> raw) {
        return raw.isPrimitive() ? MethodType.methodType(raw).wrap().returnType() : raw;
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
