package com.github.netx;

/**
 * Created by wens on 15-10-29.
 */
public interface Client {

    void startup();

    ResponseFuture send(byte[] data);

    void shutdown();


}
