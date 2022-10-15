package io.github.embedded.redis.core;

public class EmbeddedRedisStarter {

    public static void main(String[] args) throws Exception {
        EmbeddedRedisConfig embeddedRedisConfig = new EmbeddedRedisConfig();
        new EmbeddedRedisServer(embeddedRedisConfig).start();
    }

}
