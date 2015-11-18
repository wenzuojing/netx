package com.github.netx;


/**
 * Created by wens on 15-10-29.
 */
public class ClientBootstrap implements MessageHandler {

    public static void main(String[] args) throws Exception {
        ClientBuilder builder = new ClientBuilder();
        builder.host("127.0.0.1").port(1980).messageHandler(new ClientBootstrap()).heartbeatEnable(true).heartbeatInterval(5000).autoConnectRetry(true);

        Client client = builder.build();
        client.startup();

        ResponseHandler future = client.send("hi".getBytes());

        System.out.println(new String(future.get()));
    }


    @Override
    public byte[] handleNormalMessage(byte[] data) {
        System.out.println("Receive a message : " + new String(data));
        return "OK".getBytes();
    }

    @Override
    public byte[] handleHeartbeatMessage(byte[] data) {
        System.out.println("Receive a heartbeat message : " + new String(data));
        return "OK".getBytes();
    }
}
