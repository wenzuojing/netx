package com.github.netx.netty;

import com.github.netx.InnerMessageHandler;
import com.github.netx.Message;
import com.github.netx.TransportManager;
import com.github.netx.util.IDUtils;
import com.github.netx.util.LoggerUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;

/**
 * Created by wens on 15-10-29.
 */
public class NettyInboundHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerUtils.getLogger();
    private final TransportManager transportManager;
    private final InnerMessageHandler innerMessageHandler;
    private final boolean isClient;

    public NettyInboundHandler(TransportManager transportManager, InnerMessageHandler innerMessageHandler, boolean isClient) {
        this.transportManager = transportManager;
        this.innerMessageHandler = innerMessageHandler;
        this.isClient = isClient;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof Message) {
            Message m = (Message) msg;
            if (innerMessageHandler != null) {
                innerMessageHandler.handleMessage(ctx.channel(), m);
            }
        }
    }


    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.debug(cause.getMessage(), cause);
        if (this.innerMessageHandler != null) {
            this.innerMessageHandler.handleFail(ctx.channel(), cause);
        }
        ctx.close();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        if (isClient) {
            return;
        }
        long id = IDUtils.id();
        NettyTransport s = new NettyTransport(id, ctx.channel());
        if (transportManager != null) {
            transportManager.add(s);
        }
        logger.debug("channelActive - " + s);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.debug("channelInactive - " + ctx.channel());
    }
}
