package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.ClassBuilder;
import com.justinblank.classcompiler.ClassCompiler;
import com.justinblank.classcompiler.GenericVars;
import com.justinblank.classcompiler.Method;
import org.junit.Test;

import java.util.List;

import static com.justinblank.classcompiler.lang.CodeElement.call;
import static com.justinblank.classcompiler.lang.CodeElement.thisRef;
import static org.junit.Assert.*;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

public class TestDeadCodeElimination {

    @Test
    public void testIsolatedPrivateMethodsAreEliminated() throws Exception {
        String className = TestClassUtil.uniqueClassName();
        ClassBuilder classBuilder = new ClassBuilder(className, "");
        classBuilder.addEmptyConstructor();

        var methodName = TestClassUtil.uniqueMethodName();
        var privateMethod = new Method(methodName, List.of(), "I", new GenericVars(), ACC_PRIVATE);
        privateMethod.returnValue(3);
        classBuilder.addMethod(privateMethod);

        Class<?> cls = new ClassCompiler(classBuilder).generateClass();
        var o = cls.newInstance();
        assertNotNull(o);
        assertThrows(NoSuchMethodException.class, () -> cls.getDeclaredMethod(methodName));
    }

    @Test
    public void testCalledPrivateMethodsAreNotEliminated() throws Exception {
        String className = TestClassUtil.uniqueClassName();
        ClassBuilder classBuilder = new ClassBuilder(className, "");
        classBuilder.addEmptyConstructor();

        var methodName = TestClassUtil.uniqueMethodName();
        var privateMethod = new Method(methodName, List.of(), "I", new GenericVars(), ACC_PRIVATE);
        privateMethod.returnValue(3);
        classBuilder.addMethod(privateMethod);

        var publicMethod = new Method(TestClassUtil.uniqueMethodName(), List.of(), "I", new GenericVars());
        publicMethod.returnValue(call(methodName, Builtin.I, thisRef()));
        classBuilder.addMethod(publicMethod);

        Class<?> cls = new ClassCompiler(classBuilder).generateClass();
        var instance = cls.newInstance();
        var declaredMethod = cls.getDeclaredMethod(publicMethod.methodName);
        Object result = declaredMethod.invoke(instance);
        assertEquals(3, result);
    }

    @Test
    public void testChainOfPrivateMethodCallsPreservesAllInstances() throws Exception {
        String className = TestClassUtil.uniqueClassName();
        ClassBuilder classBuilder = new ClassBuilder(className, "");
        classBuilder.addEmptyConstructor();
        var methodName1 = TestClassUtil.uniqueMethodName();
        var privateMethod1 = new Method(methodName1, List.of(), "I", new GenericVars(), ACC_PRIVATE);
        privateMethod1.returnValue(3);
        classBuilder.addMethod(privateMethod1);

        var methodName2 = TestClassUtil.uniqueMethodName();
        var privateMethod2 = new Method(methodName2, List.of(), "I", new GenericVars(), ACC_PRIVATE);
        privateMethod2.returnValue(call(methodName1, Builtin.I, thisRef()));
        classBuilder.addMethod(privateMethod2);

        var methodName3 = TestClassUtil.uniqueMethodName();
        var privateMethod3 = new Method(methodName3, List.of(), "I", new GenericVars(), ACC_PRIVATE);
        privateMethod3.returnValue(call(methodName2, Builtin.I, thisRef()));
        classBuilder.addMethod(privateMethod2);

        var callingMethod = new Method(TestClassUtil.uniqueMethodName(), List.of(), "I", new GenericVars());
        callingMethod.returnValue(call(methodName1, Builtin.I, thisRef()));
        classBuilder.addMethod(callingMethod);

        Class<?> cls = new ClassCompiler(classBuilder).generateClass();
        var instance = cls.newInstance();
        var declaredMethod = cls.getDeclaredMethod(callingMethod.methodName);
        Object result = declaredMethod.invoke(instance);
        assertEquals(3, result);
    }

    @Test
    public void testMutuallyRecursivePrivateMethodsAreEliminated() throws Exception {
        String className = TestClassUtil.uniqueClassName();
        ClassBuilder classBuilder = new ClassBuilder(className, "");
        classBuilder.addEmptyConstructor();

        var methodName1 = TestClassUtil.uniqueMethodName();
        var privateMethod1 = new Method(methodName1, List.of(), "I", new GenericVars(), ACC_PRIVATE);
        classBuilder.addMethod(privateMethod1);

        var methodName2 = TestClassUtil.uniqueMethodName();
        var privateMethod2 = new Method(methodName2, List.of(), "I", new GenericVars(), ACC_PRIVATE);
        classBuilder.addMethod(privateMethod2);

        privateMethod1.returnValue(call(methodName2, Builtin.I, thisRef()));
        privateMethod2.returnValue(call(methodName2, Builtin.I, thisRef()));

        Class<?> cls = new ClassCompiler(classBuilder).generateClass();
        assertThrows(NoSuchMethodException.class, () -> cls.getDeclaredMethod(methodName1));
        assertThrows(NoSuchMethodException.class, () -> cls.getDeclaredMethod(methodName2));
    }

}
