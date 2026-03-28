package blocksmith.app.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 *
 * @author joost
 */
public class CommandRegistry {

    private final HashMap<Enum<?>, Supplier<Command>> registry = new HashMap<>();

    public CommandRegistry() {

    }
    
    public void registerAll(Map<Enum<?> , Supplier<Command>> commands) {
        registry.putAll(commands);
    }

    public void register(Enum<?> id, Supplier<Command> supplier) {
        registry.put(id, supplier);
    }

    public Optional<Command> create(Enum<?> id) {
        var supplier = registry.get(id);
        if (supplier == null) {
            return Optional.empty();
        }
        return Optional.of(supplier.get());
    }

}
