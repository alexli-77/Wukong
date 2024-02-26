package alex.utils;

import alex.exception.WukongException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
//        OBJECT_MAPPER.enable(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);
//        OBJECT_MAPPER.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
//        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        OBJECT_MAPPER.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
//        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(),true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
        // 不用@JsonSerialize和@JsonDeserialize注解时通过下面的代码注册解析器
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(GeoDistanceQueryBuilder.class, new GeoDistanceQueryBuilder.GeoDistanceSerializer());
//        module.addDeserializer(GeoDistanceQueryBuilder.class, new GeoDistanceQueryBuilder.GeoDistanceJsonDeserializer());
//        OBJECT_MAPPER.registerModule(module);
    }

    public static String toJsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new WukongException(e);
        }
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static <T> T toObject(String str, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            throw new WukongException(e);
        }
    }
    public static JsonNode toObject(String str) {
        try {
            return OBJECT_MAPPER.readTree(str);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(String str, TypeReference<T> t) {
        try {
            return OBJECT_MAPPER.readValue(str, t);
        } catch (IOException e) {
            throw new WukongException(e);
        }
    }

    public static <T> T toObject(JsonParser jsonParser, TypeReference<T> t) {
        try {
            return OBJECT_MAPPER.readValue(jsonParser, t);
        } catch (IOException e) {
            throw new WukongException(e);
        }
    }

    public static Map<String, Object> convertJsonNode2Map(JsonNode jsonNode) {
        try {
            return OBJECT_MAPPER.convertValue(jsonNode, Map.class);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Object> convertJsonNode2List(JsonNode jsonNode) {
        try {
            return OBJECT_MAPPER.convertValue(jsonNode, List.class);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertObject(Object object, Class<T> clazz) {
        return OBJECT_MAPPER.convertValue(object, clazz);
    }

    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return OBJECT_MAPPER.createArrayNode();
    }

}
