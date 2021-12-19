package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.GenericVars;
import com.justinblank.classcompiler.Method;

import java.util.List;

import static com.justinblank.classcompiler.lang.BinaryOperator.*;
import static com.justinblank.classcompiler.lang.CodeElement.*;
import static com.justinblank.classcompiler.lang.Literal.literal;

public class TestMethods {

    public static final String TEST_METHOD = "testThingMethod";

    public static Method returnLiteral() {
        var method = new Method(TEST_METHOD, List.of(), "I", null);
        method.returnValue(literal(1));
        return method;
    }

    public static Method setAndGetVariable() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", literal(1));
        method.returnValue(read("a", Type.I));
        return method;
    }

    public static Method addition() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.returnValue(plus(literal(1), literal(2)));
        return method;
    }

    public static Method equality() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.returnValue(eq(literal(1), literal(2)));
        return method;
    }

    public static Method trivialLoop() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", literal(1));
        method.loop(eq(literal(5), read("a", Type.I)),
                List.of(set("a", plus(read("a", Type.I), literal(1)))));
        method.returnValue(read("a", Type.I));
        return method;
    }

    public static Method loopWithSkip() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", literal(1));
        method.loop(eq(literal(5), read("a", Type.I)),
                List.of(set("a", plus(read("a", Type.I), literal(1))),
                        skip()));
        method.returnValue(read("a", Type.I));
        return method;
    }

    public static Method loopWithEscape() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", literal(1));
        method.loop(eq(literal(5), read("a", Type.I)),
                List.of(set("a", plus(read("a", Type.I), literal(1))),
                        escape()));
        method.returnValue(read("a", Type.I));
        return method;
    }

    public static Method nestedLoop() {
        var vars = new GenericVars();
        vars.addVar("a");
        vars.addVar("b");
        vars.addVar("c");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", literal(0));
        method.set("c", literal(1));
        method.loop(eq(literal(3), read("a", Type.I)),
                List.of(set("a", plus(read("a", Type.I), literal(1))),
                        set("b", literal(1)),
                        new Loop(eq(literal(3), read("b", Type.I)),
                                List.of(set("c", mul(read("c", Type.I), literal(2))),
                                        set("b", plus(read("b", Type.I), literal(1)))))));
        method.returnValue(read("c", Type.I));
        return method;
    }

    public static Method callNoArgMethod() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.returnValue(call("return0", thisRef()));
        return method;
    }

    public static Method callOneArgMethod() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.returnValue(call("return0", literal(0), thisRef()));
        return method;
    }

    public static Method callTwoArgMethod() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.returnValue(call("return0", literal(0), literal(1), thisRef()));
        return method;
    }
}
