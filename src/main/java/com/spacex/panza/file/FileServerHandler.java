package com.spacex.panza.file;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;

import java.io.RandomAccessFile;

public class FileServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        RandomAccessFile raf = null;

        long length = -1;
        try {
            raf = new RandomAccessFile(s, "r");
            length = raf.length();
        } catch (Exception e) {
            channelHandlerContext.writeAndFlush("ERR:" + e.getClass().getSimpleName() + ":" + e.getMessage() + "\n");
            return;
        } finally {
            if (length < 0 && raf != null) {
                raf.close();
            }
        }

        channelHandlerContext.write("OK" + raf.length() + "\n");
        if (channelHandlerContext.pipeline().get(SslHandler.class) == null) {
            channelHandlerContext.write(new DefaultFileRegion(raf.getChannel(), 0, length));
        } else {
            channelHandlerContext.write(new ChunkedFile(raf));
        }

        channelHandlerContext.write("\n");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("HELLO: type the path of the file to received!\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            ctx.write("ERR:" + cause.getClass().getSimpleName() + ":" + cause.getMessage()).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
