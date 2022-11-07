/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

import java.util.concurrent.TimeUnit;

public class LettuceSetExTest {

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
    void testSetExCase1() throws InterruptedException {
        RedisCommands<String, String> commands = client.connect().sync();
        commands.setex("k1", 1, "v1");
        TimeUnit.SECONDS.sleep(1);
        Assertions.assertNull(commands.get("k1"));
    }

    @Test
    void testSetExCase2() throws InterruptedException {
        RedisCommands<String, String> commands = client.connect().sync();
        commands.setex("k1", 5, "v1");
        TimeUnit.SECONDS.sleep(1);
        Assertions.assertEquals("v1", commands.get("k1"));
    }

}
