package com.github.netx.netty;

import com.github.netx.*;
import com.github.netx.util.IDUtils;
import com.github.netx.util.LoggerUtils;
import com.github.netx.util.Threads;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wens on 15-10-29.
 */
public class NettyClient implements Client, InnerMessageHandler, Runnable {
    private static final Logger LOGGER = LoggerUtils.getLogger();
    private final NettyTransportManager transportManager = new NettyTransportManager();

    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private ScheduledExecutorService heartbeatService;

    private ResponseMessageHandler responseMessageHandler;
    private MessageHandler messageHandler;
    private HeartbeatMessageFactory heartbeatMessageFactory;
    private boolean heartbeatEnable = false;
    private int heartbeatInterval = 15000;
    private boolean autoConnectRetry = false;

    private String host;
    private int port = 1980;
    private int workerThreads = 16;


    public NettyClient(String host, int port, MessageHandler messageHandler, HeartbeatMessageFactory heartbeatMessageFactory) {
        this.host = host;
        this.port = port;
        this.messageHandler = messageHandler;
        this.heartbeatMessageFactory = heartbeatMessageFactory;
        this.responseMessageHandler = new DefaultResponseHandler();
    }


    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public HeartbeatMessageFactory getHeartbeatMessageFactory() {
        return heartbeatMessageFactory;
    }

    public void setHeartbeatMessageFactory(HeartbeatMessageFactory heartbeatMessageFactory) {
        this.heartbeatMessageFactory = heartbeatMessageFactory;
    }

    public boolean isHeartbeatEnable() {
        return heartbeatEnable;
    }

    public void setHeartbeatEnable(boolean heartbeatEnable) {
        this.heartbeatEnable = heartbeatEnable;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public boolean isAutoConnectRetry() {
        return autoConnectRetry;
    }

    public void setAutoConnectRetry(boolean autoConnectRetry) {
        this.autoConnectRetry = autoConnectRetry;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public synchronized void startup() {

        if (heartbeatMessageFactory == null) {
            heartbeatMessageFactory = new DefaultHeartbeatMessageFactory();
        }

        if (messageHandler == null) {
            throw new IllegalStateException("messageHandler must not be null,please set the messageHandler.");
        }

        if (bootstrap != null) {
            LOGGER.error("Bootstrap was running.");
            System.exit(-1);
        }
        workerGroup = new NioEventLoopGroup(workerThreads, Threads.makeThreadFactory("Server/worker"));
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldPrepender(4, false));
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            ch.pipeline().addLast(new MessageEncoder());
                            ch.pipeline().addLast(new MessageDecoder());
                            ch.pipeline().addLast(new NettyInboundHandler(transportManager, NettyClient.this, true));
                        }
                    });

            for(int i = 0 ; i < Runtime.getRuntime().availableProcessors() * 2 ; i++ ){
                Channel channel = bootstrap.connect(host, port).sync().channel();
                transportManager.add(new NettyTransport( IDUtils.id() ,channel));
            }
        } catch (Exception e) {
            LOGGER.error("Startup fail." , e );
            shutdown();
            System.exit(-1);
        }

        //startup heartbeat
        if (heartbeatEnable && heartbeatService == null) {
            heartbeatService = Executors.newSingleThreadScheduledExecutor(Threads.makeThreadFactory("Server/heartbeat"));
            heartbeatService.scheduleWithFixedDelay(this, heartbeatInterval, heartbeatInterval, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public ResponseHandler send(byte[] data) {

        Transport transport = getOneTransport(transportManager.all());
        if (transport == null) {
            throw new IllegalStateException("Not available transport.");
        }

        Message message = new Message(IDUtils.id(), data, false, true);
        ResponseHandler future = new ResponseHandler(30000, transport.getId());
        responseMessageHandler.putResponseHandler(message.getId(), future);
        transport.sendMessage(message);
        return future;
    }

    private Transport getOneTransport(Collection<Transport> all) {
        Random random = new Random();
        return new ArrayList<>(all).get(random.nextInt(all.size()));
    }

    public void connect() {
        if (bootstrap == null) {
            throw new RuntimeException("The client not startup.");
        }
        try {
            bootstrap.connect(host, port).sync();
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }

    public synchronized void shutdown() {

        try {
            if (heartbeatService != null) {
                LOGGER.info("Shutdown heartbeat service");
                heartbeatService.shutdown();
                heartbeatService = null;
            }
        } catch (Exception e) {

        }

        try {
            LOGGER.info("Shutdown channel");
            transportManager.close();
        } catch (Exception e) {

        }

        LOGGER.info("Shutdown worker group");
        workerGroup.shutdownGracefully();

        workerGroup = null;
        bootstrap = null;
    }


    @Override
    public void run() {
        try {
            Collection<Transport> transports = transportManager.all();

            LOGGER.debug("[HEARTBEAT] Active transports = " + transports.size());

            if (transports.isEmpty() && autoConnectRetry) {
                LOGGER.debug("[HEARTBEAT] retry connect to = " + host + ":" + port);
                connect();
            }

            for (Transport s : transports) {
                if (s.isActive() && s.isWritable()) {
                    byte[] heartbeatMessage = heartbeatMessageFactory.createHeartbeatMessage();

                    Message message = Message.DEAFAULT_REQUEST_HEARBEAT_MESSAGE;

                    if (heartbeatMessage != null) {
                        message = new Message(0, heartbeatMessage, true, true);
                    }
                    s.sendMessage(message);
                }
            }
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }


    @Override
    public void handleMessage(Channel channel, Message message) {

        if (messageHandler != null && message.isRequest()) {

            byte[] responseData;
            boolean heartbeat = false;
            if (message.isHeartbeat()) {
                heartbeat = true;
                responseData = messageHandler.handleHeartbeatMessage(message.getData());
            } else {
                responseData = messageHandler.handleNormalMessage(message.getData());
            }
            channel.write(new Message(message.getId(), responseData, heartbeat, false));
            return;
        }


        if (!message.isRequest()) {
            responseMessageHandler.receive(message);
        }
    }

    @Override
    public void handleFail(Channel channel, Throwable throwable) {
        Transport transport = transportManager.get(channel);
        if (transport == null) {
            return;
        }
        responseMessageHandler.fail(transport.getId(), throwable);
    }
}
