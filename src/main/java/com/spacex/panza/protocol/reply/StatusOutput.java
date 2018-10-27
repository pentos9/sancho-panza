package com.spacex.panza.protocol.reply;

import com.google.common.base.Charsets;
import com.spacex.panza.protocol.IRedisOutput;
import io.netty.buffer.ByteBuf;

public class StatusOutput implements IRedisOutput {
    // StatusOutput demo: +status\r\n

    private String state;

    public StatusOutput(String state) {
        this.state = state;
    }

    @Override
    public void encode(ByteBuf byteBuf) {
        byteBuf.writeByte('+');
        byteBuf.writeBytes(state.getBytes(Charsets.UTF_8));
        byteBuf.writeByte(RedisProtocol.CR);
        byteBuf.writeByte(RedisProtocol.LF);
    }


    public static StatusOutput of(String state) {
        return new StatusOutput(state);
    }

    public static final StatusOutput OK = new StatusOutput("OK");
    public static final StatusOutput PONG = new StatusOutput("PONG");
}
