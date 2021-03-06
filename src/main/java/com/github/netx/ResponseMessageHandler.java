package com.github.netx;

/**
 * Created by wens on 15/11/1.
 */
public interface ResponseMessageHandler {

    void putResponseHandler(long messageId, ResponseFuture responseFuture);

    void receive(Message message);

    void fail(long transportId, Throwable throwable);
}
