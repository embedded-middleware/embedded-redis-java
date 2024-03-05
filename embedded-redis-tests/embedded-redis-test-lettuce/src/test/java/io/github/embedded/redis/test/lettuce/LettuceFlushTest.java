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

import java.util.List;

public class LettuceFlushTest {

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
    void testFlushDB() {
        RedisCommands<String, String> commands = client.connect().sync();
        commands.set("k1", "v1");
        commands.set("k2", "v2");
        List<String> keys = commands.keys("*");
        Assertions.assertTrue(keys.size() > 0);
        commands.flushdb();
        List<String> result = commands.keys("*");
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFlushAll() {
        RedisCommands<String, String> commands = client.connect().sync();
        commands.set("k1", "v1");
        commands.set("k2", "v2");
        List<String> keys = commands.keys("*");
        Assertions.assertFalse(keys.isEmpty());
        commands.flushall();
        List<String> result = commands.keys("*");
        Assertions.assertTrue(result.isEmpty());
    }

}
