package com.github.netx;

import java.util.Collection;
import java.util.List;

/**
 * Created by wens on 15-10-29.
 */
public interface TransportManager {

    public void add(Transport transport);

    public Transport get(long id);

    public Collection<Transport> all();

    public void close();

    public int size();

    public boolean isEmpty();

}
