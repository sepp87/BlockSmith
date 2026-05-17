package blocksmith.app.workspace;

import blocksmith.app.inbound.TypeResolver;
import blocksmith.domain.connection.PortRef;
import blocksmith.domain.graph.Graph;
import blocksmith.domain.graph.GraphDiff;
import blocksmith.domain.graph.GraphFactory;
import blocksmith.domain.graph.TypeEnv;
import blocksmith.domain.value.ValueType;
import blocksmith.domain.value.ValueType.ListType;
import blocksmith.domain.value.ValueType.MapType;
import blocksmith.domain.value.ValueType.SimpleType;
import blocksmith.domain.value.ValueType.VarType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author joost
 */
public class TypeSession implements TypeResolver {

    private final List<Consumer<Map<PortRef, ValueType>>> listeners = new ArrayList<>();
    private TypeEnv current;
    private Graph graph;

    public TypeSession(Graph graph) {
        this.graph = graph;
    }
    
    public ValueType typeOf(PortRef ref) {
        return current.typeOf(ref);
    }

    public void start() {
        onGraphChanged(GraphFactory.createEmpty(), graph);
    }

    public void onGraphChanged(Graph oldGraph, Graph newGraph) {
        graph = newGraph;

        var diff = GraphDiff.compare(oldGraph, newGraph);
        var shouldRebuild = evaluate(diff);
        if (shouldRebuild) {
            var previous = current;
            current = TypeEnv.of(newGraph);
            var updatedTypes = filterUpdatedTypes(graph, previous, current);
            typeEnvUpdated(updatedTypes);
        }

    }

    private static Map<PortRef, ValueType> filterUpdatedTypes(Graph graph, TypeEnv previous, TypeEnv current) {
        var updatedTypes = new HashMap<PortRef, ValueType>();

        var ports = varTypedPorts(graph);
        for (var port : ports) {
            var currentType = current.typeOf(port);

            if (previous == null || !previous.contains(port)) {
                updatedTypes.put(port, currentType);
                continue;
            }

            var previousType = previous.typeOf(port);
            if (!previousType.equals(currentType)) {
                updatedTypes.put(port, currentType);
            }
        }
        return updatedTypes;
    }

    private static List<PortRef> varTypedPorts(Graph graph) {
        var result = new ArrayList<PortRef>();
        for (var block : graph.blocks()) {
            for (var port : block.ports()) {
                if (isVarTyped(port.valueType())) {
                    result.add(PortRef.of(block, port));
                }
            }
        }
        return result;
    }

    private static boolean isVarTyped(ValueType type) {
        return switch (type) {
            case SimpleType s ->
                false;
            case VarType v ->
                true;
            case ListType l ->
                isVarTyped(l.elementType());
            case MapType m -> {
                yield isVarTyped(m.keyType()) || isVarTyped(m.elementType());
            }
        };
    }

    private static boolean evaluate(GraphDiff diff) {
        return !diff.addedBlocks().isEmpty()
                || !diff.addedConnections().isEmpty()
                || !diff.removedBlocks().isEmpty()
                || !diff.removedConnections().isEmpty();
    }

    public void addTypeEnvListener(Consumer<Map<PortRef, ValueType>> listener) {
        listeners.add(listener);
    }

    private void typeEnvUpdated(Map<PortRef, ValueType> env) {
        listeners.forEach(c -> c.accept(env));
    }

}
