package blocksmith.infra.blockloader.extractor;

import blocksmith.domain.block.BlockId;
import blocksmith.domain.block.OutputExtractor;
import blocksmith.domain.connection.PortRef;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class RecordOutputExtractor implements OutputExtractor {

    private static final Logger LOGGER = Logger.getLogger(RecordOutputExtractor.class.getName());

    private record ValueGetter(String valueId, MethodHandle handle) {

    }

    private final List<ValueGetter> getters;

    public RecordOutputExtractor(Map<String, MethodHandle> getters) {
        this.getters = getters.entrySet().stream()
                .map(e -> new ValueGetter(e.getKey(), e.getValue()))
                .toList();
    }

    public Map<PortRef, Object> extract(BlockId block, Object result) {
        var values = new HashMap<PortRef, Object>();
        var extracted = extractValues(result);


        var size = getters.size();
        for(int i = 0; i < size; i ++) {
            var getter = getters.get(i);
            var value = extracted.get(i);
            values.put(PortRef.output(block, getter.valueId), value);
        }

        return values;
    }

    private List<?> extractValues(Object result) {
        if (result instanceof List<?> list) {
            var collector = collector();
            for (var item : list) {
                var i = 0;
                var extracted = extractValues(item);
                if (extracted.size() != getters.size()) {
                    throw new IllegalStateException("Inconsistent extraction shape");
                }
                for (var extract : extracted) {
                    var siblings = collector.get(i);
                    siblings.add(extract);
                    i++;
                }
            }
            return collector;
        } else {
            return extractValuesFromRecord(result);
        }
    }

    private List<List<Object>> collector() {
        var size = getters.size();
        var collector = new ArrayList<List<Object>>(size);
        for (int i = 0; i < size; i++) {
            collector.add(new ArrayList<Object>());
        }
        return collector;
    }

    private List<Object> extractValuesFromRecord(Object result) {
        var list = new ArrayList<Object>(getters.size());
        for (var getter : getters) {
            Object value = null;
            try {
                value = getter.handle.invoke(result);
            } catch (Throwable ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            }
            list.add(value);
        }
        return list;
    }
}
