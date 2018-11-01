package com.spacex.panza.telnet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TelnetClient {

    public static final boolean SSL = System.getProperty("ssl") != null;
    public static final String HOST = System.getProperty("host", "127.0.0.1");
    public static final int PORT = Integer.parseInt(System.getProperty("8992", "8023"));

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        SslContext sslContext = null;

        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new TelnetServerInitializer(sslContext));

            Channel channel = b.connect(HOST, PORT).sync().channel();
            ChannelFuture lastChannelFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                String line = in.readLine(); //读取输入内容
                lastChannelFuture = channel.writeAndFlush(line + "\r\n");
                if (StringUtils.equalsIgnoreCase("exit", line)) {
                    channel.closeFuture().sync();
                    break;
                }
            }

            for (; ; ) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                lastChannelFuture = channel.writeAndFlush(line + "\r\n");
                if (StringUtils.equalsIgnoreCase("bye", line)) {
                    channel.closeFuture().sync();
                    break;
                }
            }

            if (lastChannelFuture != null) {
                lastChannelFuture.sync();
            }

        } finally {
            group.shutdownGracefully();
        }
    }
}
