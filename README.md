# netx
netx是一个基于tcp网络通讯组件

### USAGE
Server 
```java
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
```
Client
```java
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
```
