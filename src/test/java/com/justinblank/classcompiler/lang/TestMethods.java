package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.CompilerUtil;
import com.justinblank.classcompiler.GenericVars;
import com.justinblank.classcompiler.Method;

import java.io.PrintStream;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
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
    public static final int NEVER_RETURNED = 0xF0000000; // -268435456

    public static Method noOpVoidMethod() {
        var method = new Method(TEST_METHOD, List.of(), Void.VOID, null);
        return method.returnVoid();
    }

    public static Method callingVoidMethod() {
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, null);
        method.call("println", Void.VOID, getStatic("out", ReferenceType.of(System.class),
                ReferenceType.of(PrintStream.class)), literal(1));
        method.returnValue(1);
        return method;
    }

    public static Method returnLiteral() {
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, null);
        method.returnValue(1);
        return method;
    }

    public static Method returnChar() {
        var method = new Method(TEST_METHOD, List.of(), Builtin.C, null);
        method.returnValue((int) 'a');
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

    public static Method setAndGetCharVariable() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), Builtin.C, vars);
        method.set("a", (int) 'a');
        method.returnValue(read("a"));
        return method;
    }

    public static Method setAndGetMultipleVariables() {
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, new GenericVars("a", "b", "c", "d"));
        method.set("a", 1);
        method.set("b", read("a"));
        method.set("c", read("b"));
        method.set("d", 2);
        method.returnValue(read("d"));
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

    public static Method loopWithAndInCondition() {
        var vars = new GenericVars("i");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("i", 0).
                loop(and(gte(read("i"), 0), lt(read("i"), 5)),
                        List.of(set("i", plus(read("i"), 1))));
        method.returnValue(read("i"));
        return method;
    }

    public static Method loopDFAThingy() {
        var vars = new GenericVars("string", "index", "char", "state");
        var method = new Method(TEST_METHOD, List.of(CompilerUtil.descriptor(String.class)), Builtin.I, vars);
        method.set("index", 0).set("state", 0);
        method.loop(neq(call("length", Builtin.I, read("string")),
                        read("index")),
                List.of(set("char", call("charAt", Builtin.C, read("string"),
                        read("index"))),
                        cond(gte(read("char"), 57)).withBody(
                                returnValue(-1))
                        .orElse(
                                switchStatement(read("char"))
                                        .setCase((int) '0', returnValue(0))
                                        .setCase((int) '1', returnValue(1))
                                        .setDefault(returnValue(-2))
                        )));
        method.returnValue(1);
        return method;
    }

    public static Method denseIntegerSwitchMethod() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("a", 0);
        method.addSwitch(read("a")).
                setCase(0, List.of(returnValue(2))).
                        setCase(1, List.of(returnValue(3))).
                setDefault(returnValue(5));

        return method;
    }

    public static Method denseIntegerSwitchMethodWithAssignments() {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("a", 0);
        method.addSwitch(read("a")).
                setCase(0, set("a", 5)).
                setCase(1, set("a", 6)).
                setDefault(returnValue(4));

        method.returnValue(read("a"));
        return method;
    }

    public static Method conditionalWithSwitchAndOrElse() {
        var vars = new GenericVars("a");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("a", 0);
        method.cond(gte(read("a"), 5))
                .withBody(new Switch(read("a"))
                        .setCase(0, List.of(
                                set("a", 1), returnValue(read("a"))))
                        .setCase(1, List.of(set("a", NEVER_RETURNED), returnValue(read("a"))))
                        .setDefault(returnValue(NEVER_RETURNED)))
                .orElse(set("a", -1));
        method.returnValue(read("a"));
        return method;
    }

    public static Method conditionalWithSwitchInOrElse() {
        var vars = new GenericVars("a");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("a", 0);
        method.cond(gte(read("a"), 5))
                .withBody(set("a", -1))
                .orElse(new Switch(read("a"))
                        .setCase(0, List.of(
                                set("a", 1), returnValue(read("a"))))
                        .setCase(1, List.of(set("a", NEVER_RETURNED), returnValue(read("a"))))
                        .setDefault(returnValue(NEVER_RETURNED)));
        method.returnValue(read("a"));
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

    public static Method conditionWithElseIf() {
        var vars = new GenericVars("i");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("i", 2);
        method.cond(eq(read("i"), 3))
                .withBody(List.of(returnValue(3)))
                .elseif(eq(read("i"), 5)).withBody(List.of(returnValue(4)));
        method.returnValue(9);
        return method;
    }

    public static Method conditionWithElse() {
        var vars = new GenericVars("i");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("i", 2);
        method.cond(eq(read("i"), 3))
                .withBody(List.of(returnValue(3)))
                .orElse(returnValue(4));
        return method;
    }

    public static Method conditionalNonEqWithElse() {
        var vars = new GenericVars("i");
        var method = new Method(TEST_METHOD, List.of(), Builtin.I, vars);
        method.set("i", 2);
        method.cond(neq(read("i"), 3))
                .withBody(List.of(returnValue(3)))
                .orElse(returnValue(4));
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

    public static Method arrayLength(Type type) {
        var method = new Method(TEST_METHOD, List.of(), type, new GenericVars("a"));
        method.set("a", newArray(5, type));
        method.returnValue(CodeElement.arrayLength(read("a")));
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


    public static Method testCallMethodReturningChar() {
        var method = new Method(TEST_METHOD, List.of(CompilerUtil.descriptor(String.class)), Builtin.C, new GenericVars("s"));
        method.returnValue(call("charAt", Builtin.C, read("s"), literal(1)));
        return method;
    }

    public static Method testCallMethodReturningCharAfterStoringInLocalVariable() {
        var method = new Method(TEST_METHOD, List.of(CompilerUtil.descriptor(String.class)), Builtin.C, new GenericVars("s", "c"));
        method.set("c", call("charAt", Builtin.C, read("s"), literal(1)));
        method.returnValue(read("c"));
        return method;
    }
}
