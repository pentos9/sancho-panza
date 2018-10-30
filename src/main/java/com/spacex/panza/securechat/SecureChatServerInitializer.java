package com.spacex.panza.securechat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

public class SecureChatServerInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslContext;

    public SecureChatServerInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        channelPipeline.addLast(sslContext.newHandler(socketChannel.alloc()));
        channelPipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        channelPipeline.addLast(new StringDecoder());
        channelPipeline.addLast(new StringEncoder());
        channelPipeline.addLast(new SecureChatServerHandler());
    }
}
