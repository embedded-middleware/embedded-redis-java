package io.github.embedded.redis.test.jedis;

import io.github.embedded.redis.core.EmbeddedRedisConfig;
import io.github.embedded.redis.core.EmbeddedRedisServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

@Slf4j
class JedisSetGetTest {

    private static EmbeddedRedisServer redisServer;

    private static Jedis jedis;

    @BeforeAll
    static void beforeAll() throws Exception {
        redisServer = new EmbeddedRedisServer(new EmbeddedRedisConfig().port(0));
        redisServer.start();
        // wait for redis server start
        Thread.sleep(5_000);
        jedis = new Jedis("localhost", redisServer.getPort());
    }

    @AfterAll
    static void afterAll() throws Exception {
        jedis.close();
        redisServer.close();
    }

    @Test
    void testSetCase1() {
        jedis.set("k1", "v1");
        Assertions.assertEquals("v1", jedis.get("k1"));
    }

}
