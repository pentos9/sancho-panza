package com.spacex.panza.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        run(6379);
    }

    /**
     * Talk To This Redis Proxy With Command: redis-cli -h 127.0.0.1 -p 6379
     * Only Support Commands: set key value | get key
     *
     * @param port
     * @throws InterruptedException
     */
    public static void run(int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            channelPipeline.addLast(new RedisCommandDecoder());
                            channelPipeline.addLast(new RedisReplyEncoder());
                            channelPipeline.addLast(new RedisCommandHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            // release all resources
            group.shutdownGracefully();
        }

    }
}
