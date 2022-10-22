package io.github.embedded.redis.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class JacksonService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    public static String toJson(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            log.error("json process error, exception is ", e);
        }
        return "";
    }

    public static <T> T toPojo(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            log.error("json process error, exception is ", e);
        }
        return null;
    }

    public static <T> T toPojo(byte[] jsonBytes, Class<T> type) {
        try {
            return MAPPER.readValue(new String(jsonBytes, StandardCharsets.UTF_8), type);
        } catch (Exception e) {
            log.error("json process error, exception is ", e);
        }
        return null;
    }

    public static <T> T toRefer(String json, TypeReference<T> reference) {
        try {
            return MAPPER.readValue(json, reference);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> toList(String json, TypeReference<List<T>> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (Exception e) {
            return null;
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }

    public static ObjectNode createObjectNode() {
        return MAPPER.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return MAPPER.createArrayNode();
    }


}
