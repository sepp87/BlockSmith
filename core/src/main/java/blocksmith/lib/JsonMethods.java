package blocksmith.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import blocksmith.utils.ParsingUtils;
import blocksmith.infra.blockloader.annotations.Block;
import com.google.gson.JsonArray;
import java.util.regex.Pattern;

/**
 *
 * @author Joost
 */
public class JsonMethods {

    public static final JsonParser PARSER = new JsonParser();
    public static final com.google.gson.Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Block(
            type = "Json.asStringList",
            category = "Core",
            description = "Converts a JSON array into a list of string values.")
    public static List<String> asStringList(String jsonArray) {

        JsonElement element = PARSER.parse(jsonArray);
        List<String> list = new ArrayList<>();
        if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                String value = toOutput(item);
                list.add(value);
            }
        }
        return list;
    }

    @Block(
            type = "Json.asList",
            category = "Core",
            description = "Converts a JSON array into a list of string values.")
    public static List<?> asList(String jsonArray) {
        if (jsonArray == null) {
            throw new NullPointerException("Input string \"jsonArray\" is null");
        }
        JsonElement element = PARSER.parse(jsonArray);
        return parseJsonElement(element);
    }

    private static List<?> parseJsonElement(JsonElement element) {
        List<Object> list = new ArrayList<>();
        if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                if (item.isJsonObject()) {
                    list.add(new Gson().fromJson(item, Map.class));
                } else if (item.isJsonArray()) {
                    list.add(parseJsonElement(item));
                } else if (item.isJsonPrimitive()) {
                    JsonPrimitive primitive = item.getAsJsonPrimitive();
                    if (primitive.isString()) {
                        list.add(primitive.getAsString());
                    } else if (primitive.isNumber()) {
                        Number number = primitive.getAsNumber().doubleValue();
                        list.add(ParsingUtils.castToBestNumericType(number));
                    } else if (primitive.isBoolean()) {
                        list.add(primitive.getAsBoolean());
                    }
                } else {
                    list.add(null);
                }
            }
            return list;
        }
        throw new IllegalArgumentException("Input string \"jsonArray\" is not an array");
    }

    @Block(
            type = "Json.getKey",
            category = "Core",
            description = "Returns the element with the specified key in this JSON object.")
    public static String getKey(String json, String key) {
        return PARSER.parse(json).getAsJsonObject().get(key).toString();
    }

    @Block(
            type = "Json.getIndex",
            category = "Core",
            description = "Returns the element as string at the specified position in this JSON array.")
    public static String getIndex(String json, int index) {
        return PARSER.parse(json).getAsJsonArray().get(index).toString();
    }

    @Block(
            type = "Json.getPath",
            category = "Core",
            description = "Returns the element with the specified path in this JSON object.")
    public static String getPath(String json, String path) {
        JsonElement element = PARSER.parse(json);
        String[] parts = path.split("\\.");
        List<JsonElement> results = resolve(element, parts, 0);

        if (results.size() == 1) {
            return toOutput(results.get(0));
        }
        var array = new JsonArray();
        results.forEach(array::add);
        return GSON.toJson(array);
    }

    private static String toOutput(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return element.getAsString(); // raw value, no quotes
        }
        return element.toString(); // objects, arrays, numbers, booleans stay as-is
    }

    private static List<JsonElement> resolve(JsonElement element, String[] parts, int depth) {
        if (depth >= parts.length) {
            return List.of(element);
        }

        String part = parts[depth];

        if (part.contains("[") && part.contains("]")) {
            String key = part.substring(0, part.indexOf("["));
            if (!key.isEmpty()) {
                element = element.getAsJsonObject().get(key);
            }

            // Extract all bracket groups: [0], [*], [2], etc.
            List<String> indices = new ArrayList<>();
            var m = Pattern.compile("\\[([^]]+)]").matcher(part);
            while (m.find()) {
                indices.add(m.group(1));
            }

            return applyIndices(element, indices, 0, parts, depth);
        } else {
            element = element.getAsJsonObject().get(part);
            return resolve(element, parts, depth + 1);
        }
    }

    private static List<JsonElement> applyIndices(
            JsonElement element, List<String> indices, int idx,
            String[] parts, int depth) {

        if (idx >= indices.size()) {
            return resolve(element, parts, depth + 1);
        }

        String index = indices.get(idx);

        if ("*".equals(index)) {
            var array = element.getAsJsonArray();
            List<JsonElement> collected = new ArrayList<>();
            for (JsonElement child : array) {
                collected.addAll(applyIndices(child, indices, idx + 1, parts, depth));
            }
            return collected;
        } else {
            int i = Integer.parseInt(index);
            element = element.getAsJsonArray().get(i);
            return applyIndices(element, indices, idx + 1, parts, depth);
        }
    }

    @Block(
            type = "Json.toJson",
            category = "Core",
            description = "This method serializes the specified object into its equivalent Json representation.")
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }
}
