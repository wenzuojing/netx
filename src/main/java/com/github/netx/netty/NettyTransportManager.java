package com.github.netx.netty;

import com.github.netx.Transport;
import com.github.netx.TransportClosedListener;
import com.github.netx.TransportManager;
import com.github.netx.util.LoggerUtils;
import io.netty.channel.Channel;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wens on 15-10-29.
 */
public class NettyTransportManager implements TransportManager {

    private final Logger logger = LoggerUtils.getLogger();
    private final Map<Long, Transport> transports = new ConcurrentHashMap<Long, Transport>();

    private TransportClosedListener closedListener = new TransportClosedListener() {
        @Override
        public void onClosed(Transport transport) {
            logger.debug("[REMOVE] transport {} was closed." + transport);
            remove(transport);
        }
    };

    @Override
    public void add(Transport transport) {
        if (transport == null) {
            return;
        }
        transports.put(transport.getId(), transport);
        NettyTransport s = ((NettyTransport) transport);
        s.addCloseListener(closedListener);
    }

    @Override
    public Transport get(long id) {
        return transports.get(id);
    }

    public Transport get(Channel channel) {
        for (Transport s : transports.values()) {
            NettyTransport nettyTransport = (NettyTransport) s;
            if (channel.equals(nettyTransport.channel())) {
                return nettyTransport;
            }
        }
        return null;
    }

    @Override
    public Collection<Transport> all() {
        return transports.values();
    }

    public void remove(Transport transport) {
        if (transport == null) {
            return;
        }
        transports.remove(transport.getId());
        NettyTransport s = ((NettyTransport) transport);
        s.removeCloseListener(closedListener);
    }

    public void close() {
        for (Transport s : transports.values()) {
            s.close();
        }
    }

    @Override
    public int size() {
        return transports.size();
    }

    @Override
    public boolean isEmpty() {
        return transports.isEmpty();
    }


}
