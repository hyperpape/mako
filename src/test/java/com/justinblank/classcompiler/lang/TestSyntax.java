package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestSyntax {

    private static int classNumber = 0;

    @Test
    public void testReturnLiteral() throws Exception {
        apply(TestMethods.returnLiteral());
    }

    @Test
    public void testReturnLong() throws Exception {
        apply(TestMethods.returnLong());
    }

    @Test
    public void testNoArgConstructor() throws Exception {
        apply(TestMethods.returnNewDate());
    }

    @Test
    public void testOneArgConstructor() throws Exception {
        apply(TestMethods.callOneArgumentConstructor());
    }

    @Test
    public void testReturnThis() throws Exception {
        apply("TestReturnThis", TestMethods.returnThis());
    }

    @Test
    public void testNewArray() throws Exception {
        apply(TestMethods.returnNewByteArray());
    }

    @Test
    public void testNewArrayOfReferenceType() throws Exception {
        apply(TestMethods.returnNewArrayOfReferenceType());
    }

    @Test
    public void testReturnNewArrayOfArrays() throws Exception {
        apply(TestMethods.returnNewArrayOfArrays());
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
        apply(TestBinaryOperators.addition());
    }

    @Test
    public void testFloatAddition() throws Exception {
        apply(TestBinaryOperators.floatAddition());
    }

    @Test
    public void testEquality() throws Exception {
        apply(TestBinaryOperators.equality());
    }

    @Test
    public void testModulus() throws Exception {
        apply(TestBinaryOperators.intModulus(5, 2));
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
    public void testReadStatic() throws Exception {
        apply(TestMethods.readStatic());
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
    public void testStaticCallUsingClassAsArgument() throws Exception {
        apply(TestMethods.staticCallUsingClassAsArgument());
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

    @Test
    public void testIntCasts() throws Exception {
        apply(TestCastMethods.castIntMethod(Builtin.I));
        apply(TestCastMethods.castIntMethod(Builtin.F));
        apply(TestCastMethods.castIntMethod(Builtin.D));
        apply(TestCastMethods.castIntMethod(Builtin.L));
    }

    @Test
    public void testFloatCasts() throws Exception {
        apply(TestCastMethods.castFloatMethod(Builtin.I));
        apply(TestCastMethods.castFloatMethod(Builtin.F));
        apply(TestCastMethods.castFloatMethod(Builtin.D));
        apply(TestCastMethods.castFloatMethod(Builtin.L));
    }

    @Test
    public void testLongCasts() throws Exception {
        apply(TestCastMethods.castLongMethod(Builtin.I));
        apply(TestCastMethods.castLongMethod(Builtin.F));
        apply(TestCastMethods.castLongMethod(Builtin.D));
        apply(TestCastMethods.castLongMethod(Builtin.L));
    }

    @Test
    public void testDoubleCasts() throws Exception {
        apply(TestCastMethods.castDoubleMethod(Builtin.I));
        apply(TestCastMethods.castDoubleMethod(Builtin.F));
        apply(TestCastMethods.castDoubleMethod(Builtin.D));
        apply(TestCastMethods.castDoubleMethod(Builtin.L));
    }

    static void apply(Method method) throws Exception {
        apply("TestSyntaxTestClass" + classNumber++, method);
    }

    static void apply(String className, Method method) throws Exception {
        var builder = new ClassBuilder(className, "java/lang/Object", new String[]{});
        builder.addMethod(method);
        builder.addMethod(builder.addEmptyConstructor());

        var cls = new ClassCompiler(builder);
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
        builder.addMethod(builder.addEmptyConstructor());
        var cls = new ClassCompiler(builder);
        Class<?> compiled = cls.generateClass();
        compiled.getConstructors()[0].newInstance();
    }
}
