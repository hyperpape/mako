package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.ClassBuilder;
import com.justinblank.classcompiler.ClassCompiler;
import com.justinblank.classcompiler.Method;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.justinblank.classcompiler.lang.TestMethods.TEST_METHOD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSemantics {

    private static int classNumber = 0;

    @Test
    public void testReturnLiteral() throws Exception {
        apply(TestMethods.returnLiteral(), 1);
    }

    @Test
    public void testReturnNewArray() throws Exception {
        apply(TestMethods.returnNewByteArray(), new byte[0]);
    }

    @Test
    public void testReturnNewArrayOfReferenceType() throws Exception {
        apply(TestMethods.returnNewArrayOfReferenceType(), new String[1]);
    }

    @Test
    public void testReturnNewArrayOfArrays() throws Exception {
        apply(TestMethods.returnNewArrayOfArrays(), new byte[1][]);
    }

    @Test
    public void testNoArgConstructor() throws Exception {
        var date = call(TestMethods.returnNewDate());
        assertTrue(date instanceof Date);
    }

    @Test
    public void testOneArgConstructor() throws Exception {
        var sb = call(TestMethods.callOneArgumentConstructor());
        assertEquals("", sb.toString());
    }

    @Test
    public void testStringBuilderToString() throws Exception {
        var s = call(TestMethods.stringBuilderToString());
        assertEquals("", s);
    }

    @Test
    public void testCallMethodOnReferenceTypeReturningReferenceType() throws Exception {
        var s = call(TestMethods.stringBuilderAppend());
        assertEquals("1", s);
    }

    @Test
    public void testReadWriteReferenceTypeLocalVariables() throws Exception {
        var s = call(TestMethods.readWriteLocalVariableStringBuilder());
        assertEquals("", s);
    }

    @Test
    public void testSetAndReadVars() throws Exception {
        apply(TestMethods.setAndGetVariable(), 1);
    }

    @Test
    public void testAddition() throws Exception {
        apply(TestMethods.addition(), 3);
    }

    @Test
    public void testEquality() throws Exception {
        apply(TestMethods.equality(), 0); // TODO
    }

    @Test
    public void testTrivialLoop() throws Exception {
        apply(TestMethods.trivialLoop(), 5);
    }

    @Test
    public void testLoopWithSkip() throws Exception {
        apply(TestMethods.loopWithSkip(), 5);
    }

    @Test
    public void testLoopWithEscape() throws Exception {
        apply(TestMethods.loopWithEscape(), 2);
    }

    @Test
    public void testStaticCall() throws Exception {
        apply(TestMethods.staticCall(), 0);
    }

    @Test
    public void testNestedLoop() throws Exception {
        apply(TestMethods.nestedLoop(), 64);
    }

    @Test
    public void testCallNoArgMethod() throws Exception {
        var return0 = new Method("return0", List.of(), "I", null);
        return0.returnValue(1);
        apply(1, TestMethods.callNoArgMethod(), List.of(), return0);
    }

    @Test
    public void testConditional() throws Exception {
        apply(TestMethods.conditional(), 3);
    }

    @Test
    public void testTwoSequentialConditionals() throws Exception {
        apply(TestMethods.twoSequentialConditionals(), 3);
    }

    @Test
    public void testThreeSequentialConditionals() throws Exception {
        apply(TestMethods.threeSequentialConditionals(), 3);
    }

    @Test
    public void testCallOneArgMethod() throws Exception {
        var return0 = new Method("return0", List.of("I"), "I", null);
        return0.returnValue(1);
        apply(1, TestMethods.callOneArgMethod(), List.of(), return0);
    }

    @Test
    public void testTwoArgCall() throws Exception {
        var return0 = new Method("return0", List.of("I", "I"), "I", null);
        return0.returnValue(1);
        apply(1, TestMethods.callTwoArgMethod(), List.of(), return0);
    }

    @Test
    public void testFibonacci() throws Exception {
        var builder = new ClassBuilder("TestSemanticsTestClass" + classNumber++, "java/lang/Object", new String[]{});
        builder.addMethod(TestMethods.fibonacci());
        builder.addMethod(builder.addEmptyConstructor());
        var cls = new ClassCompiler(builder);
        Class<?> compiled = cls.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        var output = compiled.getMethod(TEST_METHOD, List.of(int.class).toArray(new Class[0])).invoke(instance, 5);
        assertEquals(8, output);
    }

    @Test
    public void testArgumentOfReferenceType() throws Exception {
        var builder = new ClassBuilder("TestSemanticsTestClass" + classNumber++, "java/lang/Object", new String[]{});
        builder.addMethod(TestMethods.argumentOfReferenceType());
        builder.addMethod(builder.addEmptyConstructor());
        var cls = new ClassCompiler(builder);
        Class<?> compiled = cls.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        var output = compiled.getMethod(TEST_METHOD, List.of(String.class).toArray(new Class[0])).invoke(instance, "abc");
        assertEquals("abc", output);
    }

    static void apply(Method method, Object o) throws Exception {
        Object output = call(method);
        Class c = o.getClass();
        // TODO: improve comparison
        if (c.getName().startsWith("[")) {
            assertEquals(c, output.getClass());
        }
        else {
            assertEquals(o, output);
        }
    }

    private static Object call(Method method) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        var builder = new ClassBuilder("TestSemanticsTestClass" + classNumber++, "java/lang/Object", new String[]{});
        builder.addMethod(method);
        builder.addMethod(builder.addEmptyConstructor());
        var cls = new ClassCompiler(builder);
        Class<?> compiled = cls.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        return compiled.getMethod(TEST_METHOD).invoke(instance);
    }

    static void apply(Object o, Method method, List<Object> arguments, Method...methods) throws Exception {
        Object output = call(method, arguments, methods);
        assertEquals(o, output);
    }

    private static Object call(Method method, List<Object> arguments, Method[] methods) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        var builder = new ClassBuilder("TestSemanticsTestClass" + classNumber++, "java/lang/Object", new String[]{});
        builder.addMethod(method);
        for (var otherMethod : methods) {
            builder.addMethod(otherMethod);
        }
        builder.addMethod(builder.addEmptyConstructor());
        var cls = new ClassCompiler(builder);
        Class<?> compiled = cls.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        List<Class<?>> clsArgs = new ArrayList<>();
        for (var obj : arguments) {
            clsArgs.add(obj.getClass());
        }
        return compiled.getMethod(TEST_METHOD, clsArgs.toArray(new Class[0])).invoke(instance, arguments.toArray());
    }
}
