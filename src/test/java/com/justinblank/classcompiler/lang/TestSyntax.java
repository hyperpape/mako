package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.*;
import org.junit.Test;

import java.util.List;

public class TestSyntax {

    private static int classNumber = 0;

    @Test
    public void testReturnLiteral() throws Exception {
        apply(TestMethods.returnLiteral());
    }

    @Test
    public void testReturnThis() throws Exception {
        apply("TestReturnThis", TestMethods.returnThis());
    }

    @Test
    public void testNewArray() throws Exception {
        apply(TestMethods.returnNewArray());
    }

    @Test
    public void testArraySetAndGet() throws Exception {
        apply(TestMethods.arraySetAndGet());
    }

    @Test
    public void testSetAndReadVars() throws Exception {
        apply(TestMethods.setAndGetVariable());
    }

    @Test
    public void testAddition() throws Exception {
        apply(TestMethods.addition());
    }

    @Test
    public void testEquality() throws Exception {
        apply(TestMethods.equality());
    }

    @Test
    public void testTrivialLoop() throws Exception {
        apply(TestMethods.trivialLoop());
    }

    @Test
    public void testLoopWithSkip() throws Exception {
        apply(TestMethods.loopWithSkip());
    }

    @Test
    public void testLoopWithEscape() throws Exception {
        apply(TestMethods.loopWithEscape());
    }

    @Test
    public void testReadField() throws Exception {
        apply(TestMethods.readField());
    }

    @Test
    public void testArrayLength() throws Exception {
        apply(TestMethods.arrayLength());
    }

    @Test
    public void testNestedLoop() throws Exception {
        apply(TestMethods.nestedLoop());
    }

    @Test
    public void testNoArgCall() throws Exception {
        var return0 = new Method("return0", List.of(), "I", null);
        return0.returnValue(1);
        apply(TestMethods.callNoArgMethod(), return0);
    }

    @Test
    public void testOneArgCall() throws Exception {
        var return0 = new Method("return0", List.of("I"), "I", null);
        return0.returnValue(1);
        apply(TestMethods.callOneArgMethod(), return0);
    }

    @Test
    public void testTwoArgCall() throws Exception {
        var return0 = new Method("return0", List.of("I", "I"), "I", null);
        return0.returnValue(1);
        apply(TestMethods.callTwoArgMethod(), return0);
    }

    @Test
    public void testConditional() throws Exception {
        apply(TestMethods.conditional());
    }

    @Test
    public void testTwoSequentialConditionals() throws Exception {
        apply(TestMethods.twoSequentialConditionals());
    }

    @Test
    public void testThreeSequentialConditionals() throws Exception {
        apply(TestMethods.threeSequentialConditionals());
    }


    @Test
    public void testRecursion() throws Exception {
        apply(TestMethods.recursion());
    }

    @Test
    public void testStaticCall() throws Exception {
        apply(TestMethods.staticCall());
    }

    @Test
    public void testMethodWithIgnoredCall() throws Exception {
        var return0 = new Method("return0", List.of(), "I", null);
        return0.returnValue(0);
        apply("SomeObject" + classNumber++, TestMethods.methodWithIgnoredCall(), return0);
    }

    @Test
    public void testFibonacci() throws Exception {
        apply(TestMethods.fibonacci());
    }

    static void apply(Method method) throws Exception {
        apply("TestSyntaxTestClass" + classNumber++, method);
    }

    static void apply(String className, Method method) throws Exception {
        var builder = new ClassBuilder(className, "java/lang/Object", new String[]{});
        builder.addMethod(method);
        builder.addMethod(builder.emptyConstructor());

        var cls = new ClassCompiler(builder, true);
        Class<?> compiled = cls.generateClass();
        compiled.getConstructors()[0].newInstance();
    }

    static void apply(Method... methods) throws Exception {
        apply("TestSyntaxTestClass" + classNumber++, methods);
    }

    static void apply(String className, Method...methods) throws Exception {
        var builder = new ClassBuilder(className, "java/lang/Object", new String[]{});
        for (var method : methods) {
            builder.addMethod(method);
        }
        builder.addMethod(builder.emptyConstructor());
        var cls = new ClassCompiler(builder, true);
        Class<?> compiled = cls.generateClass();
        compiled.getConstructors()[0].newInstance();
    }
}
