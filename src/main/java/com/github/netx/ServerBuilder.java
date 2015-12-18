package com.github.netx;

import com.github.netx.netty.NettyServer;

/**
 * Created by wens on 15-10-29.
 */
public class ServerBuilder {
    private String bind = "0.0.0.0";
    private int port = 8080;
    private int threads = 16;
    private ServerMessageHandler messageHandler;
    private boolean checksumEnable = false;

    public ServerBuilder bind(String bind) {
        this.bind = bind;
        return this;
    }

    public ServerBuilder port(int port) {
        this.port = port;
        return this;
    }


    public ServerBuilder threads(int threads) {
        this.threads = threads;
        return this;
    }

    public ServerBuilder checksumEnable(boolean checksumEnable) {
        this.checksumEnable = checksumEnable;
        return this;
    }

    public ServerBuilder messageHandler(ServerMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }

    public Server build() {
        NettyServer server = new NettyServer(bind, port, messageHandler);
        server.setMessageHandler(messageHandler);
        server.setWorkerThreads(threads);
        return server;
    }
}
