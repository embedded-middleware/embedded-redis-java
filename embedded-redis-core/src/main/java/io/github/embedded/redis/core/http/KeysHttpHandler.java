package io.github.embedded.redis.core.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.embedded.redis.core.RedisEngine;
import io.github.embedded.redis.core.util.JacksonService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class KeysHttpHandler implements HttpHandler {

    private final RedisEngine redisEngine;

    public KeysHttpHandler(RedisEngine redisEngine) {
        this.redisEngine = redisEngine;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        switch (requestMethod) {
            case "GET" -> {
                List<String> keys = redisEngine.keys("*");
                String s = String.join(",", keys);
                exchange.sendResponseHeaders(200, s.length());
                exchange.getResponseBody().write(s.getBytes(StandardCharsets.UTF_8));
            }
            case "PUT" -> {
                InputStream inputStream = exchange.getRequestBody();
                byte[] bytes = inputStream.readAllBytes();
                KeyValueModule keyValueModule = JacksonService.toPojo(bytes, KeyValueModule.class);
                if (keyValueModule == null) {
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }
                redisEngine.set(keyValueModule.getKey(), keyValueModule.getValue());
            }
            default -> exchange.sendResponseHeaders(405, 0);
        }
        exchange.close();
    }
}
