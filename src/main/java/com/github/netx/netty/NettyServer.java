package com.github.netx.netty;

import com.github.netx.*;
import com.github.netx.util.LoggerUtils;
import com.github.netx.util.Threads;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;

/**
 * Created by wens on 15-10-29.
 */
public class NettyServer implements Server, InnerMessageHandler {
    private final Logger logger = LoggerUtils.getLogger();

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture bindFuture;

    private ServerMessageHandler messageHandler;
    private String bind = "0.0.0.0";
    private int port = 1980;
    private int workerThreads = 16;


    public NettyServer(String bind, int port, ServerMessageHandler messageHandler) {
        this.bind = bind;
        this.port = port;
        this.messageHandler = messageHandler;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(ServerMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public void startup() {

        if (messageHandler == null) {
            throw new IllegalStateException("messageHandler must not be null,please set the messageHandler.");
        }

        bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), Threads.makeThreadFactory("boss"));
        workerGroup = new NioEventLoopGroup(workerThreads, Threads.makeThreadFactory("worker"));
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldPrepender(4, false));
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            ch.pipeline().addLast(new MessageDecoder());
                            ch.pipeline().addLast(new MessageEncoder());
                            ch.pipeline().addLast(new NettyInboundHandler(null, NettyServer.this, false));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 100)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            bindFuture = bootstrap.bind(bind, port).sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public synchronized void shutdown() {
        try {
            logger.info("Shutdown listener channel.");
            bindFuture.channel().close().sync();
        } catch (Exception e) {

        }

        logger.info("Shutdown worker group.");
        workerGroup.shutdownGracefully();

        logger.info("Shutdown boss group.");
        bossGroup.shutdownGracefully();


        bossGroup = null;
        workerGroup = null;
        bindFuture = null;
        bootstrap = null;
    }

    @Override
    public void handleMessage(Channel channel, Message message) {

        if (messageHandler != null && message.isRequest()) {
            if (message.isHeartbeat()) {
                channel.write(new Message(message.getId(), null, true, false));
            } else {
                channel.write(new Message(message.getId(), messageHandler.receivedMessage(message.getData()), false, false));
            }
        }
    }

    @Override
    public void handleFail(Channel channel, Throwable throwable) {
        logger.warn("Received fail : channel={}", channel, throwable);
    }
}
