package io.github.embedded.redis.core;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class RedisEngine {

    private final Map<String, byte[]> map = new ConcurrentHashMap<>();

    public RedisEngine() {
    }

    public void set(String key, byte[] value) {
        map.put(key, value);
    }

    public void set(String key, String value) {
        map.put(key, value.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] get(String key) {
        return map.get(key);
    }

    public List<String> keys(String pattern) {
        Pattern compilePattern = Pattern.compile(pattern.replace("*", ".*"));
        List<String> result = new ArrayList<>();
        for (String key : map.keySet()) {
            if (compilePattern.matcher(key).matches()) {
                result.add(key);
            }
        }
        return result;
    }

    public long delete(String key) {
        if (map.remove(key) != null) {
            return 1;
        }
        return 0;
    }

    public long delete(List<String> keys) {
        long count = 0;
        for (String key : keys) {
            if (map.remove(key) != null) {
                count++;
            }
        }
        return count;
    }
}
