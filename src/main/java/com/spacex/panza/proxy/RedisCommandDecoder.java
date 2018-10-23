package com.spacex.panza.proxy;

import com.spacex.panza.util.PrintUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class RedisCommandDecoder extends ReplayingDecoder<Void> {

    private byte[][] cmds;

    private int arg;

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if (cmds == null) {
            if (in.readByte() == '*') {
                doDecodeNumOfArg(in);
            }
        } else {
            doDecodeArgs(in);
        }

        if (isComplete()) {
            doSendCmdToHandler(out);
            doCleanUp();
        }
    }


    private void doDecodeNumOfArg(ByteBuf in) {
        // ignore negative case
        int numOfArgs = readInt(in);
        PrintUtil.println("RedisCommandDecoder NumOfArgs:" + numOfArgs);

        cmds = new byte[numOfArgs][];

        checkpoint();
    }

    /**
     * do decode arguments
     *
     * @param in
     */
    private void doDecodeArgs(ByteBuf in) {
        for (int i = arg; i < cmds.length; i++) {
            if (in.readByte() == '$') {
                int lenOfBulkStr = readInt(in);
                PrintUtil.println(String.format("RedisCommandDecoder LenOfBulkStr[%s] %s", i, lenOfBulkStr));
                cmds[i] = new byte[lenOfBulkStr];
                in.readBytes(cmds[i]);

                // skip CRLF(\r\n)
                in.skipBytes(2);
                arg++;
                checkpoint();
            } else {
                throw new IllegalStateException("Invalid Arguments!");
            }
        }
    }

    private boolean isComplete() {
        return (cmds != null) && (arg > 0) && (arg == cmds.length);
    }

    private void doSendCmdToHandler(List<Object> out) {
        PrintUtil.println("RedisCommandDecoder: send command to next handler!");
        if (cmds.length == 2) {
            out.add(new RedisCommand(new String(cmds[0]), cmds[1]));
        } else if (cmds.length == 3) {
            out.add(new RedisCommand(new String(cmds[0]), cmds[1], cmds[2]));
        } /*else {
            throw new IllegalArgumentException("Unknown command!");
        }*/
    }

    private void doCleanUp() {
        this.cmds = null;
        this.arg = 0;
    }

    private int readInt(ByteBuf in) {
        int integer = 0;

        char c;
        while ((c = (char) in.readByte()) != '\r') {
            integer = (integer * 10) + (c - '0');
        }

        if (in.readByte() != '\n') {
            throw new IllegalArgumentException("Invalid number!");
        }

        return integer;
    }
}
