/*
 * Copyright 2024 shoothzj <shoothzj@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
