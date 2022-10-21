package io.github.embedded.redis.test.lettuce;

import io.github.embedded.redis.core.EmbeddedRedisConfig;
import io.github.embedded.redis.core.EmbeddedRedisServer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class LettuceKeysTest {

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
    void testKeysStar() {
        RedisCommands<String, String> commands = client.connect().sync();
        commands.set("k1", "v1");
        commands.set("k2", "v2");
        List<String> keys = commands.keys("*");
        MatcherAssert.assertThat(keys, Matchers.containsInAnyOrder("k1", "k2"));
        Long del = commands.del("k1", "k2");
        Assertions.assertEquals(2L, del);
    }

    @Test
    void testKeysPrefix() {
        RedisCommands<String, String> commands = client.connect().sync();
        commands.set("prefix-k1", "v1");
        commands.set("k1", "v1");
        List<String> keys = commands.keys("prefix*");
        Assertions.assertEquals(1, keys.size());
        MatcherAssert.assertThat(keys, Matchers.contains("prefix-k1"));
        Long del = commands.del("prefix-k1", "k1");
        Assertions.assertEquals(2L, del);
    }

}
