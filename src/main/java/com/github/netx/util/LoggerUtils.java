package com.github.netx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wens on 15-10-29.
 */
public class LoggerUtils {

    private static final String LOG_NAME = "nb-netty";

    public static Logger getLogger() {
        return LoggerFactory.getLogger(LOG_NAME);
    }
}
