package com.spacex.panza.protocol.reply;

import com.google.common.base.Charsets;
import com.spacex.panza.protocol.IRedisOutput;
import io.netty.buffer.ByteBuf;

public class ErrorStatusOutput implements IRedisOutput {
    // ErrorStatusOutput pattern: -type reason\r\n

    private String type;
    private String reason;

    public ErrorStatusOutput(String type, String reason) {
        this.type = type;
        this.reason = reason;
    }

    public String getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public void encode(ByteBuf byteBuf) {
        byteBuf.writeByte('-');
        byteBuf.writeBytes(String.format("%s %s", type, headOf(reason)).getBytes(Charsets.UTF_8));
        byteBuf.writeByte(RedisProtocol.CR);
        byteBuf.writeByte(RedisProtocol.LF);
    }

    private String headOf(String reason) {
        int idx = reason.indexOf("\n");
        if (idx < 0) {
            return reason;
        }
        return reason.substring(0, idx).trim();
    }
}
