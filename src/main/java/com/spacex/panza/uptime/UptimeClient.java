package com.spacex.panza.uptime;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class UptimeClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

    static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectedDelay", "5"));
    static final int READ_TIME_OUT = Integer.parseInt(System.getProperty("readTimeout", "10"));
    static final Bootstrap b = new Bootstrap();
    static final UptimeClientHandler handler = new UptimeClientHandler();

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(HOST, PORT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(READ_TIME_OUT, 0, 0), handler);
                    }
                });
        b.connect();
    }


    public static void connect() {
        b.connect().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.cause() != null) {
                    handler.startTime = -1;
                    handler.println("Failed to connect:" + channelFuture.cause());
                }
            }
        });
    }
}
