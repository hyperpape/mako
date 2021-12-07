package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.*;
import org.junit.Test;

import java.util.List;

import static com.justinblank.classcompiler.lang.BinaryOperator.eq;
import static com.justinblank.classcompiler.lang.BinaryOperator.plus;
import static com.justinblank.classcompiler.lang.CodeElement.*;
import static com.justinblank.classcompiler.lang.Literal.literal;

public class TestSyntax {

    private static int classNumber = 0;

    @Test
    public void testReturnLiteral() throws Exception {
        var method = new Method("testThingMethod", List.of(), "I", null);
        method.add(returnValue(literal(1)));
        apply(method);
    }

    @Test
    public void testSetAndReadVars() throws Exception {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method("testThingMethod", List.of(), "I", vars);
        method.add(set("a", literal(1)));
        method.add(returnValue(read("a", Type.I)));
        apply(method);
    }

    @Test
    public void testAddition() throws Exception {
        var vars = new GenericVars();
        var method = new Method("testThingMethod", List.of(), "I", vars);
        method.add(returnValue(plus(literal(1), literal(2))));
        apply(method);
    }

    @Test
    public void testEquality() throws Exception {
        var vars = new GenericVars();
        var method = new Method("testThingMethod", List.of(), "I", vars);
        method.add(returnValue(eq(literal(1), literal(2))));
        apply(method);
    }

    @Test
    public void testTrivialLoop() throws Exception {
        var vars = new GenericVars();
        vars.addVar("a");
        var method = new Method("testThingMethod", List.of(), "I", vars);
        method.add(set("a", literal(1)));
        method.loop(operate(BinaryOperator.EQUALS, literal(5), read("a", Type.I)),
                List.of(set("a", plus(read("a", Type.I), literal(1)))));
        method.add(returnValue(read("a", Type.I)));
        apply(method);
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
