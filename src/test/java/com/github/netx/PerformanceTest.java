package com.github.netx;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wens on 15-12-18.
 */
public class PerformanceTest {

    public static void main(String[] args) throws InterruptedException {

        int n  = 100 ;
        final AtomicLong count = new AtomicLong() ;
        for(int i = 0 ; i < n ; i++ ){

            final int ii = i ;
            new Thread(){
                @Override
                public void run() {

                    ClientBuilder builder = new ClientBuilder();
                    builder.host("127.0.0.1").port(1980).heartbeatEnable(true).heartbeatInterval(5000).autoConnectRetry(true);

                    Client client = builder.build();
                    client.startup();
                    for(int j = 0 ;; j++ ){
                        String data  = ii+  "hi" + j  ;
                        ResponseFuture responseFuture = client.send(data.getBytes());
                        count.incrementAndGet();
                        if( data.equals( responseFuture.get() )){
                            throw new RuntimeException("fail") ;
                        }
                    }

                }
            }.start();

        }

        for(;;){
            System.out.println(count);
            count.set(0);
            Thread.sleep(5000);
        }


    }
}
