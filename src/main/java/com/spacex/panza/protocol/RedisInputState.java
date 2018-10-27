package com.spacex.panza.protocol;

import com.google.common.base.Charsets;
import com.spacex.panza.protocol.reply.InputState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.ArrayList;
import java.util.List;

public class RedisInputState extends ReplayingDecoder<InputState> {

    private static final int CR = '\r';
    private static final int LF = '\n';

    private static final int DOLLAR = '$';
    private static final int ASTERISK = '*';

    private int length;

    private List<String> params;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        InputState state = this.state();
        if (state == null) {
            this.length = readParamsLength(in);
            this.params = new ArrayList<>(length);
            state = new InputState();
            this.checkpoint(state);
        }

        for (int i = state.index; i < this.length; i++) {
            String param = readParam(in);
            this.params.add(param);
            state.index = state.index + 1;
            this.checkpoint(state);
        }

        out.add(new RedisInput(this.params));
        this.checkpoint(null);

    }

    private int readParamsLength(ByteBuf in) {

        int c = in.readByte();

        if (c != ASTERISK) {
            throw new DecoderException("expect character *");
        }

        int len = readLen(in, 3);
        if (len == 0) {
            throw new DecoderException("except non-zero params!");
        }


        return len;
    }

    private String readParam(ByteBuf in) {
        int len = readStrLen(in);
        return readStr(in, len);
    }

    private String readStr(ByteBuf in, int len) {
        if (len == 0) {
            return "";
        }

        byte[] cs = new byte[len];
        in.readBytes(cs);
        skipCRLF(in);
        return new String(cs, Charsets.UTF_8);
    }

    private int readStrLen(ByteBuf in) {

        int c = in.readByte();
        if (c != DOLLAR) {
            throw new DecoderException("except character $");
        }
        return readLen(in, 6);
    }

    private int readLen(ByteBuf in, int maxBytes) {
        byte[] digits = new byte[maxBytes];
        int len = 0;

        while (true) {
            byte d = in.getByte(in.readerIndex());
            if (!Character.isDigit(d)) {
                break;
            }

            in.readByte();
            digits[len] = d;
            len++;
            if (len > maxBytes) {
                throw new DecoderException("params length too large!");
            }
        }

        skipCRLF(in);

        if (len == 0) {
            throw new DecoderException("digit expect!");
        }


        return Integer.parseInt(new String(digits, 0, len));
    }

    private void skipCRLF(ByteBuf in) {
        int c = in.readByte();
        if (c == CR) {
            c = in.readByte();
            if (c == LF) {
                return;
            }
        }

        throw new DecoderException("except CR LF !");
    }
}
