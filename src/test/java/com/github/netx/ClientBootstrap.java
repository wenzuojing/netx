package com.github.netx;


import java.util.Scanner;

/**
 * Created by wens on 15-10-29.
 */
public class ClientBootstrap {

    public static void main(String[] args) throws Exception {
        ClientBuilder builder = new ClientBuilder();
        builder.host("127.0.0.1").port(1980).heartbeatEnable(true).heartbeatInterval(5000).autoConnectRetry(true);

        Client client = builder.build();
        client.startup();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String line = scanner.nextLine();
            if ("byte".equals(line)) {
                break;
            }
            ResponseFuture responseFuture = client.send(line.getBytes());
            System.out.println(new String(responseFuture.get()));
        }
        client.shutdown();
    }

}
