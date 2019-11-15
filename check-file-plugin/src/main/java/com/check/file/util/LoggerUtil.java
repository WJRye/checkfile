package com.check.file.util;

import com.check.file.CheckFileConstants;

import org.slf4j.Logger;

public final class LoggerUtil {

    public static Logger getDefaultLogger() {
        return getLogger(CheckFileConstants.LOG);
    }

    public static Logger getLogger(String name) {
        return org.slf4j.LoggerFactory.getLogger(name);
    }
}
