package blocksmith.domain.block;

import blocksmith.domain.connection.Connection;
import blocksmith.domain.value.Param;
import blocksmith.domain.value.Port;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    public List<Port> elements() {
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

    public ArrayBlock withFittedElements(Collection<Connection> connections) {

        var fitted = new ArrayList<Port>(fixedInputPorts());
        fitted.addAll(fitElements(connections));
        fitted.addAll(outputPorts());

        var ports = ports();
        if (fitted.size() == ports.size() && fitted.containsAll(ports)) {
            return this;
        }

        System.out.println();
        fitted.forEach(p -> System.out.println(p.valueId()));
        System.out.println();
        
        return new ArrayBlock(
                id(),
                type(),
                params(),
                fitted,
                layout()
        );
    }

    private List<Port> fitElements(Collection<Connection> connections) {
        var elements = elements();
        var connectedInputs = connectedInputs(connections);

        var elementIds = elements.stream().map(e -> e.valueId()).toList();
        var arrayId = arrayId();
        var missingIds = connectedInputs.stream()
                .filter(id -> id.startsWith(arrayId))
                .filter(id -> !elementIds.contains(id))
                .toList();
        
        if(!missingIds.isEmpty()) {
            var missing = createMissingFrom(missingIds);
            elements = Stream.concat(elements.stream(), missing.stream()).toList();
        }

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

    private List<Port> createMissingFrom(List<String> elementIds) {
        var last = elements().getFirst();
        return elementIds.stream().map(id -> last.copy(id)).toList();
    }

    private String arrayId() {
        var last = elements().getFirst();
        return last.valueId().split("#")[0];
    }

    private Port createNextFrom(Port last) {
        var index = Integer.parseInt(last.valueId().split("#")[1]) + 1;
        var argIndex = last.argIndex();
        var valueId = last.valueId().split("#")[0] + "#" + index;
        var valueType = last.valueType();
        return Port.input(valueId, argIndex, valueType, true);
    }

    private Set<String> connectedInputs(Collection<Connection> connections) {
        var result = new HashSet<String>();
        for (var connection : connections) {
            if (connection.to().blockId().equals(id())) {
                result.add(connection.to().valueId());
            }
        }
        return result;
    }

    @Override
    protected Block with(Collection<Param> params, Collection<Port> ports, BlockLayout layout) {
        return new ArrayBlock(id(), type(), params, ports, layout);
    }

    @Override
    public Block copy(BlockId id) {
        return new ArrayBlock(
                id,
                type(),
                params(),
                ports(),
                layout()
        );
    }

}
