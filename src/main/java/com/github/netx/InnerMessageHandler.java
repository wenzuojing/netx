package com.github.netx;

import io.netty.channel.Channel;

/**
 * Created by wens on 15-10-29.
 */
public interface InnerMessageHandler {

    void handleMessage(Channel channel, Message message);

    void handleFail(Channel channel, Throwable throwable);

}
