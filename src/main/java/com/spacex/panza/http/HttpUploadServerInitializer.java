package com.spacex.panza.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.ssl.SslContext;

public class HttpUploadServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;

    public HttpUploadServerInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        if (sslContext != null) {
            channelPipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
        }

        channelPipeline.addLast(new HttpRequestDecoder());
        channelPipeline.addLast(new HttpRequestEncoder());

        channelPipeline.addLast(new HttpContentCompressor());
        channelPipeline.addLast(new HttpUploadServerHandler());

    }
}