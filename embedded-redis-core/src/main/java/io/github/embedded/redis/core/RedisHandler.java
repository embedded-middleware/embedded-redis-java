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

package io.github.embedded.redis.core;

import io.github.embedded.redis.core.util.CodecUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class RedisHandler extends ChannelInboundHandlerAdapter {

    private final RedisEngine redisEngine;

    public RedisHandler(RedisEngine redisEngine) {
        this.redisEngine = redisEngine;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ArrayRedisMessage arrayRedisMessage) {
            handleArrayCommand(ctx, arrayRedisMessage);
        } else {
            log.error("Unknown type message: {}", msg);
        }
    }

    private void handleArrayCommand(ChannelHandlerContext ctx, ArrayRedisMessage arrayRedisMessage) {
        RedisMessage firstRedisMessage = arrayRedisMessage.children().get(0);
        if (firstRedisMessage instanceof FullBulkStringRedisMessage fullBulkStringRedisMessage) {
            String cmd = CodecUtil.str(fullBulkStringRedisMessage);
            try {
                CommandEnum commandEnum = CommandEnum.valueOf(cmd);
                switch (commandEnum) {
                    case SET -> handleSetCmd(ctx, arrayRedisMessage);
                    case SETEX -> handleSetExCmd(ctx, arrayRedisMessage);
                    case GET -> handleGetCmd(ctx, arrayRedisMessage);
                    case KEYS -> handleKeysCmd(ctx, arrayRedisMessage);
                    case DEL -> handleDelCmd(ctx, arrayRedisMessage);
                    case PING -> handlePingCmd(ctx, arrayRedisMessage);
                    case FLUSHDB -> handleFlushDBCmd(ctx, arrayRedisMessage);
                    case FLUSHALL -> handleFlushAllCmd(ctx, arrayRedisMessage);
                }
            } catch (IllegalArgumentException e) {
                log.error("Unknown command: {}", cmd, e);
                ctx.writeAndFlush(new ErrorRedisMessage("ERR unknown command '" + cmd + "'"));
            }
        }
    }

    private void handleSetCmd(ChannelHandlerContext ctx, ArrayRedisMessage arrayRedisMessage) {
        FullBulkStringRedisMessage keyMsg = (FullBulkStringRedisMessage) arrayRedisMessage.children().get(1);
        FullBulkStringRedisMessage valueMsg = (FullBulkStringRedisMessage) arrayRedisMessage.children().get(2);
        String key = CodecUtil.str(keyMsg);
        byte[] value = CodecUtil.bytes(valueMsg);
        redisEngine.set(key, value);
        ctx.writeAndFlush(new SimpleStringRedisMessage("OK"));
    }

    private void handleSetExCmd(ChannelHandlerContext ctx, ArrayRedisMessage arrayRedisMessage) {
        FullBulkStringRedisMessage keyMsg = (FullBulkStringRedisMessage) arrayRedisMessage.children().get(1);
        FullBulkStringRedisMessage expireMsg = (FullBulkStringRedisMessage) arrayRedisMessage.children().get(2);
        FullBulkStringRedisMessage valueMsg = (FullBulkStringRedisMessage) arrayRedisMessage.children().get(3);
        String key = CodecUtil.str(keyMsg);
        byte[] value = CodecUtil.bytes(valueMsg);
        int expire = Integer.parseInt(CodecUtil.str(expireMsg));
        redisEngine.setEx(key, value, expire);
        ctx.writeAndFlush(new SimpleStringRedisMessage("OK"));
    }

    private void handleGetCmd(ChannelHandlerContext ctx, ArrayRedisMessage arrayRedisMessage) {
        FullBulkStringRedisMessage keyMsg = (FullBulkStringRedisMessage) arrayRedisMessage.children().get(1);
        String key = CodecUtil.str(keyMsg);
        byte[] msg = redisEngine.getContent(key);
        if (msg == null) {
            ctx.writeAndFlush(FullBulkStringRedisMessage.NULL_INSTANCE);
        } else {
            ctx.writeAndFlush(new FullBulkStringRedisMessage(Unpooled.wrappedBuffer(msg)));
        }
    }

    private void handleKeysCmd(ChannelHandlerContext ctx, ArrayRedisMessage arrayRedisMessage) {
        FullBulkStringRedisMessage patternMsg = (FullBulkStringRedisMessage) arrayRedisMessage.children().get(1);
        String pattern = CodecUtil.str(patternMsg);
        List<String> keys = redisEngine.keys(pattern);
        List<RedisMessage> redisMessages = keys.stream()
                .map(key -> (RedisMessage)
                        new FullBulkStringRedisMessage(Unpooled.wrappedBuffer(key.getBytes(StandardCharsets.UTF_8))))
                .toList();
        ctx.writeAndFlush(new ArrayRedisMessage(redisMessages));
    }

    private void handleDelCmd(ChannelHandlerContext ctx, ArrayRedisMessage arrayRedisMessage) {
        List<RedisMessage> bulkStringMsgList = arrayRedisMessage.children()
                .subList(1, arrayRedisMessage.children().size());
        List<String> keys = bulkStringMsgList.stream()
                .map(redisMessage -> (FullBulkStringRedisMessage) redisMessage)
                .map(CodecUtil::str)
                .toList();
        long count = redisEngine.delete(keys);
        ctx.writeAndFlush(new IntegerRedisMessage(count));
    }

    private void handlePingCmd(ChannelHandlerContext ctx, ArrayRedisMessage arrayRedisMessage) {
        ctx.writeAndFlush(new FullBulkStringRedisMessage(
                Unpooled.wrappedBuffer("PONG".getBytes(StandardCharsets.UTF_8))));
    }

    private void handleFlushDBCmd(ChannelHandlerContext ctx, ArrayRedisMessage arrayRedisMessage) {
        redisEngine.flush();
        ctx.writeAndFlush(new SimpleStringRedisMessage("OK"));
    }

    private void handleFlushAllCmd(ChannelHandlerContext ctx, ArrayRedisMessage arrayRedisMessage) {
        redisEngine.flush();
        ctx.writeAndFlush(new SimpleStringRedisMessage("OK"));
    }

}
