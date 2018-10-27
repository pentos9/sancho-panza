package com.spacex.panza.protocol.reply;

import com.spacex.panza.protocol.IRedisOutput;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class ArrayOutput implements IRedisOutput {

    // ArrayOutput pattern: *len\r\n

    private List<IRedisOutput> outputs = new ArrayList<>();

    public static ArrayOutput newArray() {
        return new ArrayOutput();
    }

    public ArrayOutput append(IRedisOutput redisOutput) {
        outputs.add(redisOutput);
        return this;
    }

    @Override
    public void encode(ByteBuf byteBuf) {
        byteBuf.writeByte('*');
        byteBuf.writeBytes(String.valueOf(outputs.size()).getBytes());
        byteBuf.writeByte(RedisProtocol.CR);
        byteBuf.writeByte(RedisProtocol.LF);

        for (IRedisOutput output : outputs) {
            output.encode(byteBuf);
        }
    }
}
