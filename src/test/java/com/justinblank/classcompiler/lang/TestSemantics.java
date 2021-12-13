package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.ClassBuilder;
import com.justinblank.classcompiler.ClassCompiler;
import com.justinblank.classcompiler.Method;
import org.junit.Test;

import static com.justinblank.classcompiler.lang.TestMethods.TEST_METHOD;
import static org.junit.Assert.assertEquals;

public class TestSemantics {

    private static int classNumber = 0;

    @Test
    public void testReturnLiteral() throws Exception {
        apply(TestMethods.returnLiteral(), 1);
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
        apply(TestMethods.equality(), 1); // TODO
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
    public void testNestedLoop() throws Exception {
        apply(TestMethods.nestedLoop(), 64);
    }

    static void apply(Method method, Object o) throws Exception {
        method.resolve();
        var builder = new ClassBuilder("TestSemanticsTestClass" + classNumber++, "java/lang/Object", new String[]{});;
        builder.addMethod(method);
        builder.addMethod(builder.emptyConstructor());
        var cls = new ClassCompiler(builder, true);
        Class<?> compiled = cls.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        var output = compiled.getMethod(TEST_METHOD).invoke(instance);
        assertEquals(o, output);
    }
}
