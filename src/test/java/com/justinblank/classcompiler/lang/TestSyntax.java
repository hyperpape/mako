package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.*;
import org.junit.Test;

public class TestSyntax {

    private static int classNumber = 0;

    @Test
    public void testReturnLiteral() throws Exception {
        apply(TestMethods.returnLiteral());
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
    public void testNestedLoop() throws Exception {
        apply(TestMethods.nestedLoop());
    }

    static void apply(Method method) throws Exception {
        method.resolve();
        var builder = new ClassBuilder("TestSyntaxTestClass" + classNumber++, "java/lang/Object", new String[]{});;
        builder.addMethod(method);
        builder.addMethod(builder.emptyConstructor());
        var cls = new ClassCompiler(builder);
        Class<?> compiled = cls.generateClass();
        compiled.getConstructors()[0].newInstance();
    }
}
