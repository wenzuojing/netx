package com.github.netx.util;

/**
 * Created by wens on 15-10-29.
 */
public class IDUtils {

    public static long id() {
        long n = System.nanoTime();
        return n;
    }
}
