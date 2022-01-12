package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.CompilerUtil;
import com.justinblank.classcompiler.GenericVars;
import com.justinblank.classcompiler.Method;

import java.util.List;

import static com.justinblank.classcompiler.lang.ArrayRead.arrayRead;
import static com.justinblank.classcompiler.lang.BinaryOperator.*;
import static com.justinblank.classcompiler.lang.CodeElement.*;
import static com.justinblank.classcompiler.lang.Literal.literal;
import static com.justinblank.classcompiler.lang.NewArray.newArray;

public class TestMethods {

    public static final String TEST_METHOD = "testThingMethod";

    public static Method returnLiteral() {
        var method = new Method(TEST_METHOD, List.of(), "I", null);
        method.returnValue(1);
        return method;
    }

    public static Method returnThis() {
        var method = new Method(TEST_METHOD, List.of(), "LTestReturnThis;", new GenericVars());
        method.returnValue(thisRef());
        return method;
    }

    public static Method returnNewArray() {
        var method = new Method(TEST_METHOD, List.of(), "[B", new GenericVars());
        method.returnValue(newArray(literal(1), Builtin.OCTET));
        return method;
    }

    public static Method arraySetAndGet() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", newArray(literal(1), Builtin.OCTET));
        method.arraySet(read("a"), literal(0), literal(2));
        method.returnValue(arrayRead(read("a"), literal(0)));
        return method;
    }

    public static Method setAndGetVariable() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", 1);
        method.returnValue(read("a"));
        return method;
    }

    public static Method addition() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.returnValue(plus(1, 2));
        return method;
    }

    public static Method equality() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.returnValue(eq(1, 2));
        return method;
    }

    public static Method trivialLoop() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", 1);
        method.loop(eq(literal(5), read("a")),
                List.of(set("a", plus(read("a"), 1))));
        method.returnValue(read("a"));
        return method;
    }

    public static Method loopWithSkip() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", 1);
        method.loop(eq(5, read("a")),
                List.of(set("a", plus(read("a"), 1)),
                        skip()));
        method.returnValue(read("a"));
        return method;
    }

    public static Method loopWithEscape() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", 1);
        method.loop(eq(5, read("a")),
                List.of(set("a", plus(read("a"), 1)),
                        escape()));
        method.returnValue(read("a"));
        return method;
    }

    public static Method nestedLoop() {
        var vars = new GenericVars();
        vars.addVar("a");
        vars.addVar("b");
        vars.addVar("c");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", 0);
        method.set("c", 1);
        method.loop(eq(3, read("a")),
                List.of(set("a", plus(read("a"), 1)),
                        set("b", 1),
                        new Loop(eq(3, read("b")),
                                List.of(set("c", mul(read("c"), 2)),
                                        set("b", plus(read("b"), 1))))));
        method.returnValue(read("c"));
        return method;
    }

    public static Method testConditional() {
        var vars = new GenericVars();
        vars.addVar("i");
        var method = new Method(TEST_METHOD, List.of("I"), "I", vars);
        method.cond(eq(read("i"), 2)).withBody(List.of(returnValue(3)));
        method.returnValue(4);
        return method;
    }

    public static Method recursion() {
        var vars = new GenericVars();
        vars.addVar("i");
        var method = new Method(TEST_METHOD, List.of("I"), "I", vars);
        method.cond(eq(read("i"), 1)).withBody(List.of(returnValue(1)));
        method.returnValue(call(TEST_METHOD, Builtin.I, thisRef(), sub(read("i"), 1)));
        return method;
    }

    public static Method staticCall() {
        var method = new Method(TEST_METHOD, List.of(), CompilerUtil.descriptor(Integer.class), new GenericVars());
        method.returnValue(callStatic(CompilerUtil.internalName(Integer.class), "valueOf", ReferenceType.of(Integer.class),
                literal(0)));
        return method;
    }

    public static Method readField() {
        var method = new Method(TEST_METHOD, List.of(), "I", new GenericVars());
        method.returnValue(get("i", Builtin.I,
                callStatic(
                        CompilerUtil.internalName(ClassWithField.class),
                        "create", ReferenceType.of(ClassWithField.class))));
        return method;
    }

    public static Method arrayLength() {
        var method = new Method(TEST_METHOD, List.of(), "I", new GenericVars());
        method.returnValue(CodeElement.arrayLength(newArray(5, Builtin.I)));
        return method;
    }


    public static Method callNoArgMethod() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.returnValue(call("return0", Builtin.I, thisRef()));
        return method;
    }

    public static Method callOneArgMethod() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.returnValue(call("return0", Builtin.I, thisRef(), literal(0)));
        return method;
    }

    public static Method callTwoArgMethod() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.returnValue(call("return0", Builtin.I, thisRef(), literal(0), literal(1)));
        return method;
    }

    public static Method methodWithIgnoredCall() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), "I", vars);
        method.set("a", 1);
        method.loop(read("a"), List.of(
                call("returnThis", ReferenceType.of("SomeObject"), thisRef()),
                set("a",
                        plus(read("a"), 1))));
        method.call("return0", Builtin.I, thisRef());
        method.returnValue(5);
        return method;
    }
}
