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

package io.github.embedded.redis.test.lettuce;

import io.github.embedded.redis.core.EmbeddedRedisConfig;
import io.github.embedded.redis.core.EmbeddedRedisServer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class LettuceHashTest {

    private static EmbeddedRedisServer redisServer;

    private static RedisClient client;

    @BeforeAll
    static void beforeAll() throws Exception {
        redisServer = new EmbeddedRedisServer(new EmbeddedRedisConfig().port(0));
        redisServer.start();
        // wait for redis server start
        Thread.sleep(3_000);
        client = RedisClient.create(String.format("redis://localhost:%d", redisServer.getPort()));
    }

    @AfterAll
    static void afterAll() throws Exception {
        client.close();
        redisServer.close();
    }

    @Test
    void testHashOperations() {
        RedisCommands<String, String> commands = client.connect().sync();

        // HSET
        commands.hset("hashKey", "field1", "value1");
        commands.hset("hashKey", "field2", "value2");

        // HGET
        Assertions.assertEquals("value1", commands.hget("hashKey", "field1"));
        Assertions.assertEquals("value2", commands.hget("hashKey", "field2"));

        // HMSET
        Map<String, String> map = new HashMap<>();
        map.put("field3", "value3");
        map.put("field4", "value4");
        commands.hmset("hashKey", map);

        // HMGET
        Assertions.assertEquals("value3", commands.hmget("hashKey", "field3").get(0).getValue());
        Assertions.assertEquals("value4", commands.hmget("hashKey", "field4").get(0).getValue());

        // HDEL
        commands.hdel("hashKey", "field1");
        Assertions.assertNull(commands.hget("hashKey", "field1"));

        // HEXISTS
        Assertions.assertTrue(commands.hexists("hashKey", "field2"));
        Assertions.assertFalse(commands.hexists("hashKey", "field1"));

        // HKEYS
        Assertions.assertTrue(commands.hkeys("hashKey").contains("field2"));
        Assertions.assertTrue(commands.hkeys("hashKey").contains("field3"));
        Assertions.assertTrue(commands.hkeys("hashKey").contains("field4"));

        // HVALS
        Assertions.assertTrue(commands.hvals("hashKey").contains("value2"));
        Assertions.assertTrue(commands.hvals("hashKey").contains("value3"));
        Assertions.assertTrue(commands.hvals("hashKey").contains("value4"));

        // HGETALL
        Map<String, String> allFields = commands.hgetall("hashKey");
        Assertions.assertEquals("value2", allFields.get("field2"));
        Assertions.assertEquals("value3", allFields.get("field3"));
        Assertions.assertEquals("value4", allFields.get("field4"));
    }
}
