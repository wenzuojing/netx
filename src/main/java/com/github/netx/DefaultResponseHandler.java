package com.github.netx;

import com.github.netx.util.Threads;

import java.util.concurrent.*;

/**
 * Created by wens on 15/11/1.
 */
public class DefaultResponseHandler implements ResponseMessageHandler {

    private ConcurrentHashMap<Long, ResponseHandlerSchedule> responseHandlers = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduledExecutorService;

    public DefaultResponseHandler() {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2, Threads.makeThreadFactory("schedule-timeout-"));
    }

    @Override
    public void putResponseHandler(final long messageId, final ResponseHandler responseHandler) {

        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                ResponseHandlerSchedule responseHandlerSchedule = responseHandlers.remove(messageId);
                if (responseHandlerSchedule != null) {
                    ResponseHandler handler = responseHandlerSchedule.getDelegate();
                    handler.onFailed(new TimeoutException("Timeout : " + handler.getTimeout()));
                }
            }
        }, responseHandler.getTimeout(), TimeUnit.MILLISECONDS);
        responseHandlers.put(messageId, new ResponseHandlerSchedule(responseHandler, scheduledFuture));
    }

    @Override
    public void receive(Message message) {
        ResponseHandlerSchedule responseHandlerSchedule = responseHandlers.get(message.getId());
        if (responseHandlerSchedule != null) {
            ResponseHandler responseFuture = responseHandlerSchedule.getDelegate();
            responseHandlers.remove(message.getId());
            responseFuture.onReceived(message.getData());
        }
    }

    @Override
    public void fail(long transportId, Throwable throwable) {

        for (Long messageId : responseHandlers.keySet()) {
            ResponseHandlerSchedule responseHandlerSchedule = responseHandlers.remove(messageId);

            if (responseHandlerSchedule != null && responseHandlerSchedule.getDelegate().getBindTransportId() == transportId) {
                responseHandlerSchedule.getDelegate().onFailed(throwable);
            }
        }


    }
}
