package com.spacex.panza.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

@ChannelHandler.Sharable
public class RedisCommandHandler extends SimpleChannelInboundHandler<RedisCommand> {

    private Map<String, byte[]> database = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RedisCommand redisCommand) throws Exception {
        System.out.println("RedisCommandHandler:" + redisCommand);

        if (redisCommand.getName().equalsIgnoreCase("set")) {
            if (database.put(new String(redisCommand.getArg1()), redisCommand.getArg2()) == null) {
                channelHandlerContext.writeAndFlush(new IntegerReply(1));
            } else {
                channelHandlerContext.writeAndFlush(new IntegerReply(0));
            }
        } else if (redisCommand.getName().equalsIgnoreCase("get")) {
            byte[] value = database.get(new String(redisCommand.getArg1()));
            if (value != null && value.length > 0) {
                channelHandlerContext.writeAndFlush(new BulkReply(value));
            } else {
                channelHandlerContext.writeAndFlush(BulkReply.NIL_REPLY);
            }
        } else if (redisCommand.getName().equalsIgnoreCase("command")) {// redis-cli link
            channelHandlerContext.writeAndFlush(new SimpleStringReply("OK".getBytes()));
        }

        System.out.println("database:" + database);
    }
}
