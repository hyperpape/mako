package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.Method;

import java.util.List;

import static com.justinblank.classcompiler.lang.TestMethods.TEST_METHOD;

public class CompilerErrorTestMethods {

    public static Method typeMismatchReturnValue() {
        var method = new Method(TEST_METHOD, List.of(), Void.VOID, null);
        return method.returnValue(1);
    }
}
