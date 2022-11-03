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
        Thread.sleep(5_000);
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
        Assertions.assertTrue(keys.size() > 0);
        commands.flushall();
        List<String> result = commands.keys("*");
        Assertions.assertTrue(result.isEmpty());
    }

}
