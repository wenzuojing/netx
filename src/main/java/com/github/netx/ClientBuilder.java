package com.github.netx;

import com.github.netx.netty.NettyClient;

/**
 * Created by wens on 15-10-29.
 */
public class ClientBuilder {
    private String host;
    private int port = 8080;
    private int threads = 16;
    private MessageHandler messageHandler;
    private boolean heartbeatEnable = false;
    private boolean autoConnectRetry = false;
    private int heartbeatInterval = 15000;
    private HeartbeatMessageFactory heartbeatMessageFactory;

    public ClientBuilder host(String host) {
        this.host = host;
        return this;
    }

    public ClientBuilder port(int port) {
        this.port = port;
        return this;
    }

    public ClientBuilder threads(int threads) {
        this.threads = threads;
        return this;
    }

    public ClientBuilder heartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    public ClientBuilder heartbeatEnable(boolean heartbeatEnable) {
        this.heartbeatEnable = heartbeatEnable;
        return this;
    }

    public ClientBuilder messageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }

    public ClientBuilder autoConnectRetry(boolean autoConnectRetry) {
        this.autoConnectRetry = autoConnectRetry;
        return this;
    }

    public ClientBuilder heartbeatMessageFactory(HeartbeatMessageFactory heartbeatMessageFactory) {
        this.heartbeatMessageFactory = heartbeatMessageFactory;
        return this;
    }

    public Client build() {
        NettyClient client = new NettyClient(host, port, heartbeatMessageFactory);
        client.setWorkerThreads(threads);
        client.setHeartbeatEnable(heartbeatEnable);
        client.setHeartbeatInterval(heartbeatInterval);
        client.setAutoConnectRetry(autoConnectRetry);
        return client;
    }
}
