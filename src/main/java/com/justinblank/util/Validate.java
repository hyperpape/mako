package com.justinblank.util;

import org.apache.commons.lang3.StringUtils;

public class Validate {

    public static String requireNonEmpty(String s, String message) {
        if (s == null || StringUtils.isBlank(s)) {
            throw new IllegalArgumentException(message);
        }
        return s;
    }
}
