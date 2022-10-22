package io.github.embedded.redis.core;

import lombok.Getter;

@Getter
public class EmbeddedRedisConfig {

    private String host = "0.0.0.0";

    private int port = 6379;

    private int httpPort = 16379;

    public EmbeddedRedisConfig() {
    }

    public EmbeddedRedisConfig host(String host) {
        this.host = host;
        return this;
    }

    public EmbeddedRedisConfig port(int port) {
        this.port = port;
        return this;
    }

    public EmbeddedRedisConfig httpPort(int httpPort) {
        this.httpPort = httpPort;
        return this;
    }

}
