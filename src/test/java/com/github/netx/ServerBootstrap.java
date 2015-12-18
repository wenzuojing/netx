package com.github.netx;


/**
 * Created by wens on 15-10-29.
 */
public class ServerBootstrap implements ServerMessageHandler {

    public static void main(String[] args) throws InterruptedException {
        ServerBuilder builder = new ServerBuilder();
        builder.bind("0.0.0.0").port(1980).messageHandler(new ServerBootstrap());
        Server server = builder.build();
        server.startup();
        Thread.sleep(Integer.MAX_VALUE);
    }


    @Override
    public byte[] receivedMessage(byte[] data) {
        return data;
    }


}
