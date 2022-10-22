package io.github.embedded.redis.core.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;

import java.nio.charset.StandardCharsets;

public class CodecUtil {

    public static byte[] bytes(FullBulkStringRedisMessage message) {
        ByteBuf byteBuf = message.content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public static String str(FullBulkStringRedisMessage message) {
        return message.content().toString(StandardCharsets.UTF_8);
    }

}
