package com.github.netx;

import java.util.Collection;

/**
 * Created by wens on 15-10-29.
 */
public interface TransportManager {

    void add(Transport transport);

    Transport get(long id);

    Collection<Transport> all();

    void close();

    int size();

    boolean isEmpty();

}
