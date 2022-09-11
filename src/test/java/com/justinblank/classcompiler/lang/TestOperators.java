package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.GenericVars;
import com.justinblank.classcompiler.Method;
import org.junit.internal.runners.TestMethod;

import java.math.BigDecimal;
import java.util.List;

import static com.justinblank.classcompiler.lang.BinaryOperator.*;
import static com.justinblank.classcompiler.lang.CodeElement.getStatic;
import static com.justinblank.classcompiler.lang.UnaryOperator.not;

public class TestOperators {

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
        var method = new Method(TestMethods.TEST_METHOD, List.of(), Builtin.BOOL, vars);
        method.returnValue(eq(1, 2));
        return method;
    }

    public static Method referenceEquality() {
        var vars = new GenericVars();
        var method = new Method(TestMethods.TEST_METHOD, List.of(), Builtin.I, vars);
        method.returnValue(eq(
                getStatic("ZERO", ReferenceType.of(BigDecimal.class), ReferenceType.of(BigDecimal.class)),
                getStatic("ZERO", ReferenceType.of(BigDecimal.class), ReferenceType.of(BigDecimal.class))));
        return method;
    }

    public static Method referenceInequality() {
        var vars = new GenericVars();
        var method = new Method(TestMethods.TEST_METHOD, List.of(), Builtin.I, vars);
        method.returnValue(neq(
                getStatic("ZERO", ReferenceType.of(BigDecimal.class), ReferenceType.of(BigDecimal.class)),
                getStatic("ZERO", ReferenceType.of(BigDecimal.class), ReferenceType.of(BigDecimal.class))));
        return method;
    }

    public static Method intModulus(int value, int modulus) {
        var vars = new GenericVars();
        var method = new Method(TestMethods.TEST_METHOD, List.of(), Builtin.I, vars);
        method.returnValue(mod(value, modulus));
        return method;
    }

    public static Method testNotAppliedToTrue() {
        var vars = new GenericVars();
        var method = new Method(TestMethods.TEST_METHOD, List.of(), Builtin.I, vars);
        method.returnValue(not(eq(2, 2)));
        return method;
    }

    public static Method testNotAppliedToFalse() {
        var vars = new GenericVars();
        var method = new Method(TestMethods.TEST_METHOD, List.of(), Builtin.I, vars);
        method.returnValue(not(eq(2, 3)));
        return method;
    }
}
