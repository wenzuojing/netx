package com.github.netx.netty;

import com.github.netx.TransportClosedListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Collection;

/**
 * Created by wens on 15-10-29.
 */
class NettyChannelClosedListener implements GenericFutureListener<Future<Void>> {

    private NettyTransport transport;

    public NettyChannelClosedListener(NettyTransport transport) {
        this.transport = transport;
    }

    @Override
    public void operationComplete(Future<Void> future) throws Exception {
        Collection<TransportClosedListener> listeners = transport.closedListeners();
        for (TransportClosedListener l : listeners) {
            l.onClosed(transport);
        }
    }
}
