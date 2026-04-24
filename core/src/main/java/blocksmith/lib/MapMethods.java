package blocksmith.lib;

import blocksmith.infra.blockloader.annotations.Block;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joost
 */
public class MapMethods {

    @Block(
            type = "Map.create",
            aliases = {"Dictionary.create"},
            description = "Creates a new map with the specified list of keys and values.",
            category = "Core")
    public static <K, V> Map<K, V> create(List<K> keys, List<V> values) {
        var result = new HashMap<K, V>();
        var size = keys.size();
        for (int i = 0; i < size; i++) {
            var key = keys.get(i);
            var value = values.get(i);
            result.put(key, value);
        }
        return Collections.unmodifiableMap(result);
    }

    @Block(
            type = "Map.get",
            aliases = {"Dictionary.get"},
            description = "Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.",
            category = "Core")
    public static <K, V> V get(Map<K, V> map, K key) {
        return map.get(key);
    }

    @Block(
            type = "Map.getOrDefault",
            aliases = {"Dictionary.getOrDefault"},
            description = "Returns the value to which the specified key is mapped, or defaultValue if this map contains no mapping for the key.",
            category = "Core")
    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Block(
            type = "Map.size",
            aliases = {"Dictionary.size"},
            description = "Returns the number of key-value mappings in this map. If the map contains more than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.",
            category = "Core")
    public static <K, V> int size(Map<K, V> map) {
        return map.size();
    }

    @Block(
            type = "Map.keys",
            aliases = {"Dictionary.keys"},
            description = "Returns a list of the keys contained in this map.",
            category = "Core")
    public static <K, V> List<K> keys(Map<K, V> map) {
        return List.copyOf(map.keySet());
    }

    @Block(
            type = "Map.values",
            aliases = {"Dictionary.values"},
            description = "Returns a list of the values contained in this map.",
            category = "Core")
    public static <K, V> List<V> values(Map<K, V> map) {
        return List.copyOf(map.values());
    }

    @Block(
            type = "Map.merge",
            aliases = {"Dictionary.merge"},
            description = "Copies all of the mappings from \"b\" to \"a\". These mappings will replace any mappings that \"a\" had for any of the keys currently in \"b\".",
            category = "Core")
    public static <K, V> Map<K, V> merge(Map<K, V> a, Map<K, V> b) {
        var result = new HashMap<K, V>();
        result.putAll(a);
        result.putAll(b);
        return Collections.unmodifiableMap(result);
    }

    @Block(
            type = "Map.putIfAbsent",
            aliases = {"Dictionary.putIfAbsent"},
            description = "Only adds the key value pairs to the map, if the specified key is absent.",
            category = "Core")
    public static <K, V> Map<K, V> putIfAbsent(Map<K, V> map, List<K> keys, List<V> values) {
        var result = new HashMap<K, V>();
        result.putAll(map);
        var size = keys.size();
        for (int i = 0; i < size; i++) {
            var key = keys.get(i);
            var value = values.get(i);
            result.putIfAbsent(key, value);
        }
        return Collections.unmodifiableMap(result);
    }

    @SafeVarargs
    @Block(
            type = "Map.remove",
            aliases = {"Dictionary.remove"},
            description = "Removes the mapping for the specified key from this map if present.",
            category = "Core")
    public static <K, V> Map<K, V> remove(Map<K, V> map, K... keys) {
        var result = new HashMap<K, V>(map);
        for (var k : keys) {
            result.remove(k);
        }
        return Collections.unmodifiableMap(result);
    }

    @Block(
            type = "Map.containsKey",
            aliases = {"Dictionary.containsKey"},
            description = "Returns true if this map contains a mapping for the specified key.",
            category = "Core")
    public static <K, V> boolean containsKey(Map<K, V> map, K key) {
        return map.containsKey(key);
    }

    @Block(
            type = "Map.containsValue",
            aliases = {"Dictionary.containsValue"},
            description = "Returns true if this map maps one or more keys to the specified value.",
            category = "Core")
    public static <K, V> boolean containsValue(Map<K, V> map, V value) {
        return map.containsValue(value);
    }

    @Block(
            type = "Map.isEmpty",
            aliases = {"Dictionary.isEmpty"},
            description = "Returns true if this map contains no key-value mappings.",
            category = "Core")
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map.isEmpty();
    }

}
