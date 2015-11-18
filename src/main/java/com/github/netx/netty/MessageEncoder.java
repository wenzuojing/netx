package com.github.netx.netty;

import com.github.netx.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by wens on 15-10-29.
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {

    public final static int HEARTBEAT_FLAT = 1 << 0;
    public final static int REQUEST_FLAT = 1 << 1;


    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {

        int flag = 0;

        if (msg.isHeartbeat()) {
            flag = flag | HEARTBEAT_FLAT;
        }

        if (msg.isRequest()) {
            flag = flag | REQUEST_FLAT;
        }

        out.writeInt(flag);
        out.writeLong(msg.getId());
        byte[] data = msg.getData();
        int length = 0;
        if (data != null) {
            length = data.length;

        }
        out.writeInt(length);

        if (length != 0) {
            out.writeBytes(data);
        }

    }
}
