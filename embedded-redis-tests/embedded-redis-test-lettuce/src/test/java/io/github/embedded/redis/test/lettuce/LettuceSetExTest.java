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
