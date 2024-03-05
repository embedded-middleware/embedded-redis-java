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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class RedisEngine {

    private final Map<String, RedisVal> map = new ConcurrentHashMap<>();

    public RedisEngine() {
    }

    public void set(String key, byte[] value) {
        map.put(key, new RedisVal(value));
    }

    public void set(String key, String value) {
        map.put(key, new RedisVal(value.getBytes(StandardCharsets.UTF_8)));
    }

    public void setEx(String key, byte[] value, long expire) {
        map.put(key, new RedisVal(value, expire));
    }

    public RedisVal get(String key) {
        RedisVal redisVal = map.get(key);
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

    public void flush() {
        map.clear();
    }
}
