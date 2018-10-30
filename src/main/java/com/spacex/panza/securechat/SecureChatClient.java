package com.spacex.panza.securechat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SecureChatClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        final SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new SecureChatClinetInitializer(sslContext));

            Channel channel = b.connect(HOST, PORT).sync().channel();
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                lastWriteFuture = channel.writeAndFlush(line + "\r\n");
                if ("byte".equalsIgnoreCase(line)) {
                    channel.closeFuture().sync();
                    break;
                }

                if (lastWriteFuture != null) {
                    lastWriteFuture.sync();
                }
            }


        } finally {
            eventLoopGroup.shutdownGracefully();
        }

    }
}
