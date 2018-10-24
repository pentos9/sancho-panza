package com.spacex.panza.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RedisReplyEncoder extends MessageToByteEncoder<RedisReply> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RedisReply redisReply, ByteBuf out) throws Exception {
        System.out.println("RedisReplyEncoder#encode:" + redisReply);
        redisReply.write(out);
    }
}
