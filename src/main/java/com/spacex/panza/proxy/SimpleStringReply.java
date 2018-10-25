package com.spacex.panza.proxy;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class SimpleStringReply implements RedisReply<String> {

    /**
     * Simple String Reply starts with *
     */
    private static final char MARKER = '+';

    private final byte[] data;

    public SimpleStringReply(byte[] data) {
        this.data = data;
    }

    @Override
    public String data() {
        return this.data();
    }

    @Override
    public void write(ByteBuf out) throws IOException {

        if (data == null) {
            return;
        }
        out.writeByte(MARKER);
        out.writeBytes(data);
        out.writeBytes(CRLF);
    }
}
