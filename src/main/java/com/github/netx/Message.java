package com.github.netx;

/**
 * Created by wens on 15-10-29.
 */
public class Message {

    public final static Message DEFAULT_REQUEST_HEARBEAT_MESSAGE = new Message(0, null, true, true);

    private final long id;

    private final byte[] data;

    private final boolean heartbeat;

    private final boolean request;

    public Message(long id, byte[] data, boolean heartbeat, boolean request) {
        this.id = id;
        this.data = data;
        this.heartbeat = heartbeat;
        this.request = request;
    }

    public long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public boolean isRequest() {
        return request;
    }
}
