/*
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

package io.github.embedded.redis.core;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class RedisVal {

    private byte[] content;

    private long expiredNs;

    public RedisVal() {
    }

    public RedisVal(byte[] content) {
        this.content = content;
    }

    public RedisVal(byte[] content, long expireSeconds) {
        this.content = content;
        this.expiredNs = System.nanoTime() + TimeUnit.SECONDS.toNanos(expireSeconds);
    }

    public boolean isExpired() {
        return expiredNs > 0 && System.nanoTime() > expiredNs;
    }
}
