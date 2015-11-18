package com.github.netx.netty;


import com.github.netx.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by wens on 15-10-29.
 */
public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    public MessageDecoder() {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int flag = msg.readInt();
        long id = msg.readLong();
        int length = msg.readInt();

        byte[] data = new byte[length];
        if (length > 0) {
            msg.readBytes(data);
        }
        out.add(new Message(id, data,
                (flag & MessageEncoder.HEARTBEAT_FLAT) == MessageEncoder.HEARTBEAT_FLAT,
                (flag & MessageEncoder.REQUEST_FLAT) == MessageEncoder.REQUEST_FLAT
        ));
    }
}
