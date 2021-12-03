package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.ClassBuilder;
import com.justinblank.classcompiler.ClassCompiler;
import com.justinblank.classcompiler.CompilerUtil;
import com.justinblank.classcompiler.Method;
import com.justinblank.classcompiler.lang.Literal;
import com.justinblank.classcompiler.lang.BinaryOperator;
import org.junit.Test;

import java.util.List;

import static com.justinblank.classcompiler.lang.BinaryOperator.plus;
import static com.justinblank.classcompiler.lang.CodeElement.*;

public class TestSyntax {

    private static int classNumber = 0;

    @Test
    public void testReturnLiteral() throws Exception {
        var method = new Method("testThingMethod", List.of(), "I", null);
        method.add(returnValue(Literal.of(1)));
        apply(method);
    }

    @Test
    public void testThingAlt() throws Exception {
        var method = new Method("testThingMethod", List.of(), "I", null);
        method.add(set("a", Literal.of(1)));
        method.loop(operate(BinaryOperator.EQUALS, Literal.of(5), read("a")),
                List.of(set("a", plus(read("a"), Literal.of(1)))));
        method.add(returnValue(read("a")));
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
