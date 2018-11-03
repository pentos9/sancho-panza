package com.spacex.panza.telnet;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.util.Date;


@ChannelHandler.Sharable
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write(String.format("Welcome to %s !\r\n", InetAddress.getLocalHost().getHostName()));
        ctx.write(String.format("It's %s now!\r\n", new Date()));
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String request) throws Exception {
        String response = null;
        boolean close = false;

        if (StringUtils.isBlank(request)) {
            channelHandlerContext.write("please say something!\r\n");
        } else if (StringUtils.equalsIgnoreCase("bye", request)) {
            channelHandlerContext.write("have a good day! \r\n");
            close = true;
        } else {
            response = String.format("Did you say '%s' ?\r\n", request);
        }

        ChannelFuture future = channelHandlerContext.write(response);
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

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
