package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.*;
import org.junit.Test;

import java.util.List;

import static com.justinblank.classcompiler.lang.Literal.literal;

public class TestSyntax {

    private static int classNumber = 0;

    @Test
    public void testReturnLiteral() throws Exception {
        apply(TestMethods.returnLiteral());
    }

    @Test
    public void testReturnThis() throws Exception {
        apply("SomeObject", TestMethods.returnThis());
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
    public void testNestedLoop() throws Exception {
        apply(TestMethods.nestedLoop());
    }

    @Test
    public void testNoArgCall() throws Exception {
        var return0 = new Method("return0", List.of(), "I", null);
        return0.returnValue(literal(1));
        apply(TestMethods.callNoArgMethod(), return0);
    }

    @Test
    public void testOneArgCall() throws Exception {
        var return0 = new Method("return0", List.of("I"), "I", null);
        return0.returnValue(literal(1));
        apply(TestMethods.callOneArgMethod(), return0);
    }

    @Test
    public void testTwoArgCall() throws Exception {
        var return0 = new Method("return0", List.of("I", "I"), "I", null);
        return0.returnValue(literal(1));
        apply(TestMethods.callTwoArgMethod(), return0);
    }

    @Test
    public void testConditional() throws Exception {
        apply(TestMethods.testConditional());
    }

    @Test
    public void testRecursion() throws Exception {
        apply(TestMethods.recursion());
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

    static void apply(Method...methods) throws Exception {
        var builder = new ClassBuilder("TestSyntaxTestClass" + classNumber++, "java/lang/Object", new String[]{});
        for (var method : methods) {
            builder.addMethod(method);
        }
        builder.addMethod(builder.emptyConstructor());
        var cls = new ClassCompiler(builder);
        Class<?> compiled = cls.generateClass();
        compiled.getConstructors()[0].newInstance();
    }
}
