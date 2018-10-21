package com.spacex.panza.echo;


import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.UUID;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    public EchoClientHandler() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        for (int i = 0; i < 100; i++) {
            String responseContent = "[Client message]" + UUID.randomUUID().toString() + System.getProperty("line.separator");
            ByteBuf firstMessage = Unpooled.buffer((responseContent).getBytes().length);
            firstMessage.writeBytes(responseContent.getBytes());
            ctx.writeAndFlush(firstMessage);
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("[Client] [Server message] " + msg.toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
