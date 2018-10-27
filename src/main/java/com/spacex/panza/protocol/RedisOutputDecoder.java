package com.spacex.panza.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@ChannelHandler.Sharable
public class RedisOutputDecoder extends MessageToMessageDecoder<IRedisOutput> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, IRedisOutput redisOutput, List<Object> out) throws Exception {
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
        redisOutput.encode(byteBuf);
        out.add(byteBuf);
    }
}
