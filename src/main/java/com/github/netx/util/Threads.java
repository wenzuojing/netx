package com.github.netx.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wens on 15-10-29.
 */
public class Threads {

    public static ThreadFactory makeThreadFactory(final String baseName) {

        return new ThreadFactory() {
            AtomicLong count = new AtomicLong(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("%s-%d", baseName, count.incrementAndGet()));
            }
        };
    }

    public static void sleep(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
