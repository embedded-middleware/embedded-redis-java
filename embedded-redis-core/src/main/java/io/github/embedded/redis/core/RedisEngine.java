/*
 * Copyright 2024 shoothzj <shoothzj@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.embedded.redis.core;

import io.github.embedded.redis.core.model.RedisContent;
import io.github.embedded.redis.core.model.RedisSet;
import io.github.embedded.redis.core.model.RedisVal;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class RedisEngine {

    private final Map<String, RedisContent> map = new ConcurrentHashMap<>();

    public RedisEngine() {
    }

    public void set(String key, byte[] value) {
        map.put(key, new RedisContent(new RedisVal(value)));
    }

    public void set(String key, String value) {
        map.put(key, new RedisContent(new RedisVal(value.getBytes(StandardCharsets.UTF_8))));
    }

    public void setEx(String key, byte[] value, long expire) {
        map.put(key, new RedisContent(new RedisVal(value, expire)));
    }

    public RedisVal get(String key) {
        RedisContent redisContent = map.get(key);
        if (redisContent == null) {
            return null;
        }
        RedisVal redisVal = redisContent.val();
        if (redisVal != null && redisVal.isExpired()) {
            map.remove(key);
            return null;
        }
        return redisVal;
    }

    public byte[] getContent(String key) {
        RedisVal redisVal = get(key);
        if (redisVal == null) {
            return null;
        }
        return redisVal.getContent();
    }

    public long delete(String key) {
        if (map.remove(key) != null) {
            return 1;
        }
        return 0;
    }

    public void hset(String key, String field, byte[] value) {
        map.putIfAbsent(key, new RedisContent(new RedisSet()));
        RedisSet redisSet = map.get(key).set();
        redisSet.set(field, value);
    }

    public byte[] hget(String key, String field) {
        RedisSet set = map.getOrDefault(key, new RedisContent(new RedisSet())).set();
        return set != null ? set.get(field) : null;
    }

    public void hmset(String key, Map<String, byte[]> hash) {
        map.computeIfAbsent(key, k -> new RedisContent(new RedisSet())).set().setAll(hash);
    }

    public List<byte[]> hmget(String key, List<String> fields) {
        RedisSet set = map.getOrDefault(key, new RedisContent(new RedisSet())).set();
        List<byte[]> values = new ArrayList<>();
        for (String field : fields) {
            values.add(set.get(field));
        }
        return values;
    }

    public long hdel(String key, List<String> fields) {
        RedisSet set = map.getOrDefault(key, new RedisContent(new RedisSet())).set();
        long count = 0;
        for (String field : fields) {
            if (set.remove(field)) {
                count++;
            }
        }
        return count;
    }

    public boolean hexists(String key, String field) {
        RedisSet set = map.getOrDefault(key, new RedisContent(new RedisSet())).set();
        return set.exists(field);
    }

    public Set<String> hkeys(String key) {
        RedisSet set = map.getOrDefault(key, new RedisContent(new RedisSet())).set();
        return set.keys();
    }

    public List<byte[]> hvals(String key) {
        RedisSet set = map.getOrDefault(key, new RedisContent(new RedisSet())).set();
        return set.values();
    }

    public Map<String, byte[]> hgetall(String key) {
        RedisSet set = map.getOrDefault(key, new RedisContent(new RedisSet())).set();
        return set.getAll();
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

    public long delete(List<String> keys) {
        long count = 0;
        for (String key : keys) {
            if (map.remove(key) != null) {
                count++;
            }
        }
        return count;
    }

    public void flush() {
        map.clear();
    }
}
