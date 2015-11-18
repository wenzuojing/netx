package com.github.netx;


/**
 * Created by wens on 15-10-29.
 */
public class ServerBootstrap implements MessageHandler {

    public static void main(String[] args) {
        ServerBuilder builder = new ServerBuilder();
        builder.bind("0.0.0.0").port(1980).messageHandler(new ServerBootstrap());

        Server server = builder.build();
        server.startup();
    }


    @Override
    public byte[] handleNormalMessage(byte[] data) {
        System.out.println("Receive a message : " + new String(data));
        return "RECEIVED".getBytes();
    }

    @Override
    public byte[] handleHeartbeatMessage(byte[] data) {
        System.out.println("Receive a heartbeat message : " + new String(data));
        return "OK".getBytes();
    }

}
