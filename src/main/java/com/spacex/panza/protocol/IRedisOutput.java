package com.spacex.panza.protocol;

import io.netty.buffer.ByteBuf;

public interface IRedisOutput {
    void encode(ByteBuf byteBuf);
}
