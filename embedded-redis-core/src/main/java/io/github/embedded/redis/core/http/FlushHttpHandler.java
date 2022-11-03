package io.github.embedded.redis.core.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.embedded.redis.core.RedisEngine;

import java.io.IOException;

public class FlushHttpHandler implements HttpHandler {

    private final RedisEngine redisEngine;

    public FlushHttpHandler(RedisEngine redisEngine) {
        this.redisEngine = redisEngine;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if ("POST".equals(requestMethod)) {
            redisEngine.flush();
            exchange.sendResponseHeaders(200, 0);
        } else {
            exchange.sendResponseHeaders(405, 0);
        }
        exchange.close();
    }
}
