package com.github.netx;

import java.util.concurrent.CountDownLatch;

/**
 * Created by wens on 15/10/31.
 */
public class ResponseHandler {

    private final long timeout;
    private final long bindTransportId;

    private volatile byte[] data;


    private volatile Throwable throwable;

    private CountDownLatch countDownLatch;


    public ResponseHandler(long timeout, long bindTransportId) {
        this.timeout = timeout;
        this.bindTransportId = bindTransportId;
        this.countDownLatch = new CountDownLatch(1);
    }

    public void onReceived(byte[] data) {
        this.data = data;
        this.countDownLatch.countDown();
    }

    public void onFailed(Throwable t) {
        this.throwable = t;
        this.countDownLatch.countDown();
    }

    public byte[] get() {

        try {
            this.countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (throwable != null) {
            throw new RuntimeException(throwable);
        }
        return data;
    }

    public long getTimeout() {
        return timeout;
    }

    public long getBindTransportId() {
        return bindTransportId;
    }
}
