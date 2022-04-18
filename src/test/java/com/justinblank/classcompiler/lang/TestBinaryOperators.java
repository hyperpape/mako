package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.GenericVars;
import com.justinblank.classcompiler.Method;

import java.util.List;

import static com.justinblank.classcompiler.lang.BinaryOperator.*;

public class TestBinaryOperators {


    public static Method addition() {
        var vars = new GenericVars();
        var method = new Method(TestMethods.TEST_METHOD, List.of(), Builtin.I, vars);
        method.returnValue(plus(1, 2));
        return method;
    }

    public static Method floatAddition() {
        var vars = new GenericVars();
        var method = new Method(TestMethods.TEST_METHOD, List.of(), Builtin.F, vars);
        method.returnValue(plus(1.0f, 2.0f));
        return method;
    }

    public static Method equality() {
        var vars = new GenericVars();
        var method = new Method(TestMethods.TEST_METHOD, List.of(), Builtin.I, vars);
        method.returnValue(eq(1, 2));
        return method;
    }

    public static Method intModulus(int value, int modulus) {
        var vars = new GenericVars();
        var method = new Method(TestMethods.TEST_METHOD, List.of(), Builtin.I, vars);
        method.returnValue(mod(value, modulus));
        return method;
    }
}
