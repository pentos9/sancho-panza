package com.spacex.panza.protocol.reply;

import com.google.common.base.Charsets;
import com.spacex.panza.protocol.IRedisOutput;
import io.netty.buffer.ByteBuf;

public class StringOutput implements IRedisOutput {

    // StringOutput pattern:
    // null -> $-1\r\n，
    // empty -> $0\r\n\r\n，
    // common -> $len\r\ncontent\r\n

    private String content;

    public StringOutput(String content) {
        this.content = content;
    }

    @Override
    public void encode(ByteBuf byteBuf) {
        byteBuf.writeByte('$');

        if (content == null) {
            byteBuf.writeByte('-');
            byteBuf.writeByte('1');
            byteBuf.writeByte(RedisProtocol.CR);
            byteBuf.writeByte(RedisProtocol.LF);
            return;
        }

        byte[] bytes = content.getBytes(Charsets.UTF_8);
        byteBuf.writeBytes(String.valueOf(bytes.length).getBytes());
        byteBuf.writeByte(RedisProtocol.CR);
        byteBuf.writeByte(RedisProtocol.LF);
        if (content.length() > 0) {
            byteBuf.writeBytes(bytes);
        }

        byteBuf.writeByte(RedisProtocol.CR);
        byteBuf.writeByte(RedisProtocol.LF);
    }

    public static StringOutput of(String content) {
        return new StringOutput(content);
    }

    public static StringOutput of(long value) {
        return new StringOutput(String.valueOf(value));
    }

    public static final StringOutput NULL = new StringOutput(null);
}
