package blocksmith.domain.block;

import blocksmith.domain.graph.Graph;
import blocksmith.domain.value.Param;
import blocksmith.domain.value.ParamInput;
import blocksmith.domain.value.Port;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author joost
 */
public final class ArrayBlock extends Block {

    public ArrayBlock(
            BlockId id,
            String type,
            Collection<Param> params,
            Collection<Port> ports,
            BlockLayout layout) {

        super(id, type, params, ports, layout);
        System.out.println("ARRAY BLOCK " + type);
    }

    public Collection<Port> fixedInputPorts() {
        return inputPorts().stream().filter(p -> !p.isElement()).toList();
    }

    private List<Port> elements() {
        return inputPorts().stream().filter(p -> p.isElement()).toList();
    }

    public Port anyElement() {
        return elements().stream().findFirst().orElseThrow(() -> new IllegalStateException("No elements available although an ArrayBlock!"));
    }

    public Collection<Port> connectedElements() {
        var elements = elements();
        var lastActive = elements.size() - 1;
        return List.copyOf(elements.subList(0, lastActive));
    }

    public ArrayBlock withFittedElements(Graph graph) {

        var fitted = new ArrayList<Port>(inputPorts());
        fitted.addAll(fitElements(graph));
        fitted.addAll(outputPorts());

        var ports = ports();
        if (fitted.size() == ports.size() && fitted.containsAll(ports)) {
            return this;
        }

        System.out.println("WITH FITTED ELEMENTS");

        return new ArrayBlock(
                id(),
                type(),
                params(),
                fitted,
                layout()
        );
    }

    private List<Port> fitElements(Graph graph) {
        var elements = elements();
        var connectedInputs = connectedInputs(graph);

        var connectedElements = elements.stream()
                .filter(e -> connectedInputs.contains(e.valueId()))
                .toList();

        var lastConnected = connectedElements.isEmpty() ? null : connectedElements.getLast();
        var lastElement = elements.getLast();

        if (Objects.equals(lastConnected, lastElement)) {
            lastElement = createNextFrom(lastElement);
        }
        return Stream.concat(connectedElements.stream(), Stream.of(lastElement)).toList();
    }

    private Port createNextFrom(Port last) {
        var index = Integer.parseInt(last.valueId().split("#")[1]) + 1;
        var argIndex = last.argIndex();
        var valueId = last.valueId().split("#")[0] + "#" + index;
        var valueType = last.valueType();
        return Port.input(valueId, argIndex, valueType, true);
    }

    private Set<String> connectedInputs(Graph graph) {
        var result = new HashSet<String>();
        for (var connection : graph.connections()) {
            if (connection.to().blockId().equals(id())) {
                result.add(connection.to().valueId());
            }
        }
        return result;
    }

    @Override
    protected Block copy(Collection<Param> params, Collection<Port> ports, BlockLayout layout) {
        return new ArrayBlock(id(), type(), params, ports, layout);
    }

}
