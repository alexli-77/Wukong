package alex.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

/**
 * @author liyang77
 */
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
//        OBJECT_MAPPER.enable(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);

        //In JSON specification, it requires the use of double quotes for field names.
        // To enable Jackson to handle the unquoted field name, add JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES in the ObjectMapper configuration.
        OBJECT_MAPPER.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        //禁用科学计数法
        OBJECT_MAPPER.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        OBJECT_MAPPER.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

        //取消默认转换timestamps形式，否则日期时间是以时间戳形式显示
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        OBJECT_MAPPER.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
//        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        //忽略 在Json字符串中存在，但是在Java对象中不存在对应属性的情况。防止错误
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
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
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(String str, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(String str, TypeReference<T> t) {
        try {
            return OBJECT_MAPPER.readValue(str, t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode toObject(String str) {
        try {
            return OBJECT_MAPPER.readTree(str);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return OBJECT_MAPPER.createArrayNode();
    }

    public static boolean isJsonString(String str) {
        try {
            OBJECT_MAPPER.readTree(str);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public static <T> T convertValue(Object object, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.convertValue(object, clazz);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
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

    public static <K extends Object, V extends Object> Map<K, V> getJsonAsMap(String json, K key, V value) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<K, V>> typeRef = new TypeReference<Map<K, V>>() {
            };
            return mapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("Couldnt parse json:" + json, e);
        }
    }
}

