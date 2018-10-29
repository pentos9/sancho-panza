package com.spacex.panza.securechat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;

public class SecureChatServerHandler extends SimpleChannelInboundHandler<String> {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        for (Channel channel : channels) {
            if (channel != channelHandlerContext.channel()) {
                channelHandlerContext.writeAndFlush("[" + channelHandlerContext.channel().remoteAddress() + "]" + s + "\n");
            } else {
                channelHandlerContext.writeAndFlush("[You]" + s + "\n");
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
            @Override
            public void operationComplete(Future<? super Channel> future) throws Exception {
                ctx.writeAndFlush("" + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
                ctx.write("your chat is protected by" + ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() + " cipher suite\n");
                channels.add(ctx.channel());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
