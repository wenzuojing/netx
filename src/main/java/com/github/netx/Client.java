package com.github.netx;

/**
 * Created by wens on 15-10-29.
 */
public interface Client {

    void startup();

    ResponseHandler send(byte[] data);

    void shutdown();


}
