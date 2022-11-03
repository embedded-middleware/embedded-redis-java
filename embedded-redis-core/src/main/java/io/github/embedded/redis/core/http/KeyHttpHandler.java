package io.github.embedded.redis.core.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.embedded.redis.core.RedisEngine;

import java.io.IOException;

public class KeyHttpHandler implements HttpHandler {

    private final RedisEngine redisEngine;

    public KeyHttpHandler(RedisEngine redisEngine) {
        this.redisEngine = redisEngine;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String requestURI = exchange.getRequestURI().toString();
        String[] split = requestURI.split("/");
        String key = split[split.length - 1];
        switch (requestMethod) {
            case "GET" -> {
                byte[] value = redisEngine.get(key);
                exchange.sendResponseHeaders(200, value.length);
                exchange.getResponseBody().write(value);
            }
            case "DELETE" -> {
                redisEngine.delete(key);
                exchange.sendResponseHeaders(204, 0);
            }
            default -> exchange.sendResponseHeaders(405, 0);
        }
        exchange.close();
    }
}
