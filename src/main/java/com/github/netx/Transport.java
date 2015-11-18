package com.github.netx;

import java.net.SocketAddress;

/**
 * Created by wens on 15-10-29.
 */
public interface Transport {

    long getId();

    void sendMessage(Message message);

    void close();

    SocketAddress getRemoteAddress();

    boolean isActive();

    boolean isOpen();

    boolean isWritable();

}
