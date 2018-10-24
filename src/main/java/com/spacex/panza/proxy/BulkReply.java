package com.spacex.panza.proxy;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class BulkReply implements RedisReply<byte[]> {

    public static final BulkReply NIL_REPLY = new BulkReply();

    private static final char MARKER = '$';

    private final byte[] data;

    private final int len;

    public BulkReply() {
        this.data = null;
        this.len = -1;
    }

    public BulkReply(byte[] data) {
        this.data = data;
        this.len = data.length;
    }

    @Override
    public byte[] data() {
        return this.data;
    }

    @Override
    public void write(ByteBuf out) throws IOException {
        // Write Header

        out.writeByte(MARKER);
        out.writeBytes(String.valueOf(len).getBytes());

        out.writeBytes(CRLF);

        // Write Data
        if (len > 0) {
            System.out.println("BulkReply#write:" + new String(data));
            out.writeBytes(data);
            out.writeBytes(CRLF);
        }
    }
}
