package com.justinblank.classcompiler.lang;

import java.util.concurrent.atomic.AtomicInteger;

public class TestClassUtil {

    private static AtomicInteger classCounter = new AtomicInteger();

    public static String uniqueClassName() {
        return "testClass" + classCounter.incrementAndGet();
    }

    public static String uniqueMethodName() {
        return "testMethod" + classCounter.incrementAndGet();
    }
}
