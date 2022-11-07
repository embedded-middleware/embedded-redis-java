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
