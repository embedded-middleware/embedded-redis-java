/**
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
