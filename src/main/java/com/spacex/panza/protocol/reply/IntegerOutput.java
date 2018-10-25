package com.spacex.panza.protocol.reply;

import com.google.common.base.Charsets;
import com.spacex.panza.protocol.IRedisOutput;
import io.netty.buffer.ByteBuf;

public class IntegerOutput implements IRedisOutput {

    // IntegerOutput pattern=> :integer\r\n

    private long value;

    public IntegerOutput(long value) {
        this.value = value;
    }

    @Override
    public void encode(ByteBuf byteBuf) {
        byteBuf.writeByte(':');
        byteBuf.writeBytes(String.valueOf(value).getBytes(Charsets.UTF_8));
        byteBuf.writeByte(RedisProtocol.CR);
        byteBuf.writeByte(RedisProtocol.LF);
    }

    public static IntegerOutput of(long value) {
        return new IntegerOutput(value);
    }

    ;

    public static final IntegerOutput ZEOR = new IntegerOutput(0);
    public static final IntegerOutput ONE = new IntegerOutput(1);
}
