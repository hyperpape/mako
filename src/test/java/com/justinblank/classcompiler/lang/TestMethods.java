package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.CompilerUtil;
import com.justinblank.classcompiler.GenericVars;
import com.justinblank.classcompiler.Method;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.List;

import static com.justinblank.classcompiler.lang.ArrayRead.arrayRead;
import static com.justinblank.classcompiler.lang.BinaryOperator.*;
import static com.justinblank.classcompiler.lang.CodeElement.*;
import static com.justinblank.classcompiler.lang.Literal.literal;
import static com.justinblank.classcompiler.lang.NewArray.newArray;
import static com.justinblank.classcompiler.lang.UnaryOperator.not;

public class TestMethods {

    public static final String TEST_METHOD = "testThingMethod";

    public static Method noOpVoidMethod() {
        var method = new Method(TEST_METHOD, List.of(), Void.VOID, null);
        return method.returnVoid();
    }

    public static Method returnLiteral() {
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, null);
        method.returnValue(1);
        return method;
    }

    public static Method returnLong() {
        var method = new Method(TEST_METHOD, List.of(), Builtin.L, null);
        method.returnValue(1);
        return method;
    }

    public static Method returnThis() {
        var method = new Method(TEST_METHOD, List.of(), ReferenceType.of("TestReturnThis"), new GenericVars());
        method.returnValue(thisRef());
        return method;
    }

    public static Method returnNewDate() {
        var method = new Method(TEST_METHOD, List.of(), Date.class, new GenericVars());
        method.returnValue(construct(ReferenceType.of(Date.class)));
        return method;
    }

    public static Method callOneArgumentConstructor() {
        var method = new Method(TEST_METHOD, List.of(), Integer.class, new GenericVars());
        method.returnValue(construct(ReferenceType.of(Integer.class), literal(16)));
        return method;
    }

    public static Method stringBuilderToString() {
        var method = new Method(TEST_METHOD, List.of(), String.class, new GenericVars());
        method.returnValue(call("toString", ReferenceType.of(String.class),
                construct(ReferenceType.of(StringBuilder.class), literal(16))));
        return method;
    }

    public static Method stringBuilderAppend() {
        var method = new Method(TEST_METHOD, List.of(), String.class, new GenericVars());
        method.returnValue(call("toString", ReferenceType.of(String.class),
                call("append", ReferenceType.of(StringBuilder.class),
                        construct(ReferenceType.of(StringBuilder.class), literal(16)), literal(1))));
        return method;
    }

    public static Method readWriteLocalVariableStringBuilder() {
        var method = new Method(TEST_METHOD, List.of(), String.class, new GenericVars("sb"));
        method.set("sb", construct(ReferenceType.of(StringBuilder.class), literal(16)));
        method.returnValue(call("toString", ReferenceType.of(String.class),
                read("sb")));
        return method;
    }

    public static Method returnNewByteArray() {
        var method = new Method(TEST_METHOD, List.of(), "[B", new GenericVars());
        method.returnValue(newArray(literal(1), Builtin.OCTET));
        return method;
    }

    public static Method returnNewArrayOfReferenceType() {
        var method = new Method(TEST_METHOD, List.of(), "[Ljava/lang/String;", new GenericVars());
        method.returnValue(newArray(literal(1), ReferenceType.of(String.class)));
        return method;
    }

    public static Method returnNewArrayOfArrays() {
        var method = new Method(TEST_METHOD, List.of(), "[[B", new GenericVars());
        method.returnValue(newArray(literal(1), ArrayType.of(Builtin.OCTET)));
        return method;
    }

    public static Method arraySetAndGet() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("a", newArray(literal(1), Builtin.OCTET));
        method.arraySet(read("a"), literal(0), literal(2));
        method.returnValue(arrayRead(read("a"), literal(0)));
        return method;
    }

    public static Method setAndGetVariable() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("a", 1);
        method.returnValue(read("a"));
        return method;
    }

    public static Method trivialLoop() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("a", 1);
        method.loop(lt(read("a"), 5),
                List.of(set("a", plus(read("a"), 1))));
        method.returnValue(read("a"));
        return method;
    }

    public static Method loopWithSkip() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("a", 1);
        method.loop(lt( read("a"), 5),
                List.of(set("a", plus(read("a"), 1)),
                        skip()));
        method.returnValue(read("a"));
        return method;
    }

    public static Method loopWithEscape() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("a", 1);
        method.loop(lt(read("a"), 5),
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
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("a", 0);
        method.set("c", 1);
        method.loop(lt(read("a"), 3),
                List.of(set("a", plus(read("a"), 1)),
                        set("b", 1),
                        new Loop(lt( read("b"), 3),
                                List.of(set("c", mul(read("c"), 2)),
                                        set("b", plus(read("b"), 1))))));
        method.returnValue(read("c"));
        return method;
    }

    public static Method conditional() {
        var vars = new GenericVars();
        vars.addVar("i");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("i", 2);
        method.cond(eq(read("i"), 2)).withBody(List.of(returnValue(3)));
        method.returnValue(4);
        return method;
    }

    public static Method conditionWithElse() {
        var vars = new GenericVars("i");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("i", 2);
        method.cond(eq(read("i"), 3))
                .withBody(List.of(returnValue(3)))
                .orElse().withBody(List.of(returnValue(4)));
        return method;
    }

    public static Method nestedConditional() {
        var vars = new GenericVars("i");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("i", 2);
        method.cond(gt(read("i"), 0))
                .withBody(List.of(cond(gt(read("i"), 1)).withBody(returnValue(3))));
        method.returnValue(4);
        return method;
    }

    public static Method negatedConditional() {
        var vars = new GenericVars();
        vars.addVar("i");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("i", 2);
        method.cond(not(eq(read("i"), 2))).withBody(List.of(returnValue(3)));
        method.returnValue(4);
        return method;
    }

    public static Method twoSequentialConditionals() {
        var vars = new GenericVars();
        vars.addVar("i");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("i", 2);
        method.cond(eq(read("i"), 1)).withBody(List.of(returnValue(2)));
        method.cond(eq(read("i"), 2)).withBody(List.of(returnValue(3)));
        method.returnValue(4);
        return method;
    }

    public static Method threeSequentialConditionals() {
        var vars = new GenericVars();
        vars.addVar("i");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("i", 2);
        method.cond(eq(read("i"), 1)).withBody(List.of(returnValue(2)));
        method.cond(eq(read("i"), 2)).withBody(List.of(returnValue(3)));
        method.cond(eq(read("i"), 3)).withBody(List.of(returnValue(4)));
        method.returnValue(5);
        return method;
    }

    public static Method recursion() {
        var vars = new GenericVars();
        vars.addVar("i");
        var method = new Method(TEST_METHOD, List.of("I"), Builtin.I, vars);
        method.cond(eq(read("i"), 1)).withBody(List.of(returnValue(1)));
        method.returnValue(call(TEST_METHOD, Builtin.I, thisRef(), sub(read("i"), 1)));
        return method;
    }

    public static Method staticCall() {
        var method = new Method(TEST_METHOD, List.of(), Integer.class, new GenericVars());
        method.returnValue(callStatic(CompilerUtil.internalName(Integer.class), "valueOf", ReferenceType.of(Integer.class),
                literal(0)));
        return method;
    }

    public static Method staticCallUsingClassAsArgument() {
        var method = new Method(TEST_METHOD, List.of(), Integer.class, new GenericVars());
        method.returnValue(callStatic(CompilerUtil.internalName(Integer.class), "valueOf", Integer.class,
                literal(0)));
        return method;
    }

    public static Method callInterfaceMethod() {
        var vars = new GenericVars("time");
        var method = new Method(TEST_METHOD, List.of(CompilerUtil.descriptor(TemporalAccessor.class)), Builtin.I, vars);
        method.returnValue(callInterface("get", Builtin.I, read("time"),
                getStatic("MILLI_OF_SECOND", ReferenceType.of(ChronoField.class),
                        ReferenceType.of(ChronoField.class))));
        return method;
    }

    public static Method readField() {
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, new GenericVars());
        method.returnValue(get("i", Builtin.I,
                callStatic(
                        CompilerUtil.internalName(ClassWithField.class),
                        "create", ReferenceType.of(ClassWithField.class))));
        return method;
    }

    public static Method setField() {
        var method = new Method(TEST_METHOD, List.of(), Void.VOID, new GenericVars());
        method.fieldSet(
                get("i", Builtin.I,
                callStatic(
                        CompilerUtil.internalName(ClassWithField.class),
                        "create", ReferenceType.of(ClassWithField.class))), literal(0));
        method.returnVoid();
        return method;
    }

    public static Method readStatic() {
        var method = new Method(TEST_METHOD, List.of(), ReferenceType.of(Boolean.class), new GenericVars());
        method.returnValue(getStatic("TRUE", ReferenceType.of(Boolean.class), ReferenceType.of(Boolean.class)));
        return method;
    }

    public static Method arrayLength() {
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, new GenericVars());
        method.returnValue(CodeElement.arrayLength(newArray(5, Builtin.I)));
        return method;
    }


    public static Method callNoArgMethod() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.returnValue(call("return0", Builtin.I, thisRef()));
        return method;
    }

    public static Method callOneArgMethod() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.returnValue(call("return0", Builtin.I, thisRef(), literal(0)));
        return method;
    }

    public static Method callTwoArgMethod() {
        var vars = new GenericVars();
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.returnValue(call("return0", Builtin.I, thisRef(), literal(0), literal(1)));
        return method;
    }

    public static Method methodWithIgnoredCall() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("a", 1);
        method.loop(read("a"), List.of(
                call("returnThis", ReferenceType.of("SomeObject"), thisRef()),
                set("a",
                        plus(read("a"), 1))));
        method.call("return0", Builtin.I, thisRef());
        method.returnValue(5);
        return method;
    }

    public static Method argumentOfReferenceType() {
        var method = new Method(TEST_METHOD, List.of(CompilerUtil.descriptor(String.class)), String.class, new GenericVars("s"));
        method.returnValue(read("s"));
        return method;
    }

    public static Method fibonacci() {
        var method = new Method(TEST_METHOD, List.of("I"), Builtin.I, new GenericVars("x"));
        method.cond(eq(read("x"), 0)).withBody(List.of(
                returnValue(1)));
        method.cond(eq(read("x"), 1)).withBody(List.of(
                returnValue(1)));
        method.returnValue(plus(
                call(TEST_METHOD, Builtin.I, thisRef(), sub(read("x"), 1)),
                call(TEST_METHOD, Builtin.I, thisRef(), sub(read("x"), 2))));
        return method;
    }
}
