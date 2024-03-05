package io.github.embedded.redis.core.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RedisSet {
    private final ConcurrentHashMap<String, RedisVal> map;

    public RedisSet() {
        this.map = new ConcurrentHashMap<>();
    }

    public void set(String field, byte[] value) {
        map.put(field, new RedisVal(value));
    }

    public byte[] get(String field) {
        RedisVal val = map.get(field);
        return val != null ? val.getContent() : null;
    }

    public void setAll(Map<String, byte[]> hash) {
        hash.forEach((field, value) -> map.put(field, new RedisVal(value)));
    }

    public boolean remove(String field) {
        return map.remove(field) != null;
    }

    public boolean exists(String field) {
        return map.containsKey(field);
    }

    public Set<String> keys() {
        return map.keySet();
    }

    public List<byte[]> values() {
        return map.values().stream().map(RedisVal::getContent).collect(Collectors.toList());
    }

    public Map<String, byte[]> getAll() {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getContent()));
    }
}
