package com.github.netx;

import java.util.concurrent.ScheduledFuture;

/**
 * Created by wens on 15-11-18.
 */
public class ResponseHandlerSchedule {

    private ResponseHandler delegate;

    private ScheduledFuture<?> scheduledFuture;

    public ResponseHandlerSchedule(ResponseHandler responseHandler, ScheduledFuture<?> scheduledFuture) {
        this.delegate = responseHandler;
        this.scheduledFuture = scheduledFuture;
    }

    public ResponseHandler getDelegate() {
        return delegate;
    }

    public void setDelegate(ResponseHandler delegate) {
        this.delegate = delegate;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}
