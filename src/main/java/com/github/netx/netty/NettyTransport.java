package com.github.netx.netty;

import com.github.netx.Message;
import com.github.netx.Transport;
import com.github.netx.TransportClosedListener;
import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by wens on 15-10-29.
 */
public class NettyTransport implements Transport {
    private long id;
    private Channel channel;
    private final List<TransportClosedListener> listeners = new LinkedList<TransportClosedListener>();
    private final NettyChannelClosedListener channelClosedListener;

    public NettyTransport(long id, Channel channel) {
        this.id = id;
        this.channel = channel;
        this.channelClosedListener = new NettyChannelClosedListener(this);
        this.channel.closeFuture().addListener(channelClosedListener);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void sendMessage(Message message) {
        this.channel.writeAndFlush(message);
    }

    public SocketAddress getRemoteAddress() {
        return this.channel.remoteAddress();
    }

    public boolean isActive() {
        return this.channel.isActive();
    }

    public boolean isOpen() {
        return this.channel.isOpen();
    }

    public boolean isWritable() {
        return this.channel.isWritable();
    }

    @Override
    public void close() {
        try {
            this.channel.close().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Channel channel() {
        return channel;
    }

    public Collection<TransportClosedListener> closedListeners() {
        return listeners;
    }

    public void addCloseListener(TransportClosedListener listener) {
        listeners.add(listener);
    }

    public void removeCloseListener(TransportClosedListener listener) {
        listeners.remove(listener);
    }

    @Override
    public String toString() {
        return "NettyTransport{" +
                "id=" + id +
                ", channel=" + this.channel +
                '}';
    }
}
