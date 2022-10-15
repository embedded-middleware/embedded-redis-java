package io.github.embedded.redis.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisEngine {

    private final Map<String, byte[]> map = new ConcurrentHashMap<>();

    public RedisEngine() {
    }

    public void set(String key, byte[] value) {
        map.put(key, value);
    }

    public byte[] get(String key) {
        return map.get(key);
    }
}
