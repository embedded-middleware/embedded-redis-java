package io.github.embedded.redis.core;

import com.sun.net.httpserver.HttpServer;
import io.github.embedded.redis.core.http.KeyHttpHandler;
import io.github.embedded.redis.core.http.KeysHttpHandler;
import io.github.embedded.redis.core.util.SocketUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
public class EmbeddedRedisServer {

    private final EmbeddedRedisConfig embeddedRedisConfig;

    private final int listenPort;

    private final int httpListenPort;

    private final NioEventLoopGroup bossGroup;

    private final NioEventLoopGroup workerGroup;

    public EmbeddedRedisServer() throws Exception {
        this(new EmbeddedRedisConfig());
    }

    public EmbeddedRedisServer(EmbeddedRedisConfig embeddedRedisConfig) throws Exception {
        this.embeddedRedisConfig = embeddedRedisConfig;
        if (embeddedRedisConfig.getPort() == 0) {
            this.listenPort = SocketUtil.getFreePort();
        } else {
            this.listenPort = embeddedRedisConfig.getPort();
        }
        if (embeddedRedisConfig.getHttpPort() == 0) {
            this.httpListenPort = SocketUtil.getFreePort();
        } else {
            this.httpListenPort = embeddedRedisConfig.getHttpPort();
        }
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
    }

    public String getHost() {
        return embeddedRedisConfig.getHost();
    }

    public int getPort() {
        return listenPort;
    }

    public void start() throws Exception {
        RedisEngine redisEngine = new RedisEngine();
        final ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RedisDecoder());
                        pipeline.addLast(new RedisBulkStringAggregator());
                        pipeline.addLast(new RedisArrayAggregator());
                        pipeline.addLast(new RedisEncoder());
                        pipeline.addLast(new RedisHandler(redisEngine));
                    }
                });
        b.bind(listenPort).sync();
        new Thread(() -> {
            try {
                InetSocketAddress socketAddress = new InetSocketAddress("0.0.0.0", httpListenPort);
                HttpServer httpServer = HttpServer.create(socketAddress, 0);
                httpServer.createContext("/keys", new KeysHttpHandler(redisEngine));
                httpServer.createContext("/keys/", new KeyHttpHandler(redisEngine));
                httpServer.start();
            } catch (IOException e) {
                log.error("start prometheus metrics server error", e);
            }
        }).start();
        log.info("embedded redis start success. tcp listen at {} http listen at {}", listenPort, httpListenPort);
    }

    public void close() throws Exception {
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

}
