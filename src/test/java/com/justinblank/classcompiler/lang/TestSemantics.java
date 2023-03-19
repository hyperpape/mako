package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.ClassBuilder;
import com.justinblank.classcompiler.ClassCompiler;
import com.justinblank.classcompiler.Method;
import com.justinblank.util.NoOpPrintStream;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import static com.justinblank.classcompiler.lang.BinaryOperator.*;
import static com.justinblank.classcompiler.lang.TestMethods.TEST_METHOD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSemantics {

    private static int classNumber = 0;

    @Test
    public void testReturnLiteral() throws Exception {
        apply(TestMethods::returnLiteral, 1);
    }

    @Test
    public void testCallingVoidMethod() throws Exception {
        apply(TestMethods::callingVoidMethod, 1);
    }

    @Test
    public void testReturnLong() throws Exception {
        apply(TestMethods::returnLong, 1L);
    }

    @Test
    public void testReturnNewArray() throws Exception {
        apply(TestMethods::returnNewByteArray, new byte[0]);
    }

    @Test
    public void testReturnNewArrayOfReferenceType() throws Exception {
        apply(TestMethods::returnNewArrayOfReferenceType, new String[1]);
    }

    @Test
    public void testReturnNewArrayOfArrays() throws Exception {
        apply(TestMethods::returnNewArrayOfArrays, new byte[1][]);
    }

    @Test
    public void testNoArgConstructor() throws Exception {
        var date = call(TestMethods.returnNewDate());
        assertTrue(date instanceof Date);
    }

    @Test
    public void testNoOpStatement() throws Exception {
        call(TestMethods.noOpStatement(), false);
        call(TestMethods.noOpStatement(), true);
    }

    @Test
    public void testOneArgConstructor() throws Exception {
        var i = call(TestMethods.callOneArgumentConstructor());
        assertEquals(16, i);
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
    public void testReadStatic() throws Exception {
        apply(TestMethods::readStatic, Boolean.TRUE);
    }

    @Test
    public void testReadWriteReferenceTypeLocalVariables() throws Exception {
        var s = call(TestMethods.readWriteLocalVariableStringBuilder());
        assertEquals("", s);
    }

    @Test
    public void testSetAndReadVars() throws Exception {
        apply(TestMethods::setAndGetVariable, 1);
    }

    @Test
    public void testSetAndReadMultipleVars() throws Exception {
        apply(TestMethods::setAndGetMultipleVariables, 2);
    }

    @Test
    public void testAddition() throws Exception {
        apply(TestOperators::addition, 3);
    }

    @Test
    public void testEquality() throws Exception {
        apply(TestOperators::equality, false);
    }

    @Test
    public void testReferenceEquality() throws Exception {
        apply(TestOperators::referenceEquality, true);
    }

    @Test
    public void testReferenceInEquality() throws Exception {
        apply(TestOperators::referenceInequality, false);
    }

    // TODO: determine treatment of comparisons between int and double
    @Test
    public void testComparisonsOnIntegers() throws Exception {
        apply(() -> TestOperators.binaryOperator(0, 1, (l, r) -> gte(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(1, 1, (l, r) -> gte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(2, 1, (l, r) -> gte(l,r), Builtin.BOOL), true);

        apply(() -> TestOperators.binaryOperator(0, 1, (l, r) -> gt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(1, 1, (l, r) -> gt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(2, 1, (l, r) -> gt(l,r), Builtin.BOOL), true);

        apply(() -> TestOperators.binaryOperator(0, 1, (l, r) -> lte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(1, 1, (l, r) -> lte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(2, 1, (l, r) -> lte(l,r), Builtin.BOOL), false);

        apply(() -> TestOperators.binaryOperator(0, 1, (l, r) -> lt(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(1, 1, (l, r) -> lt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(2, 1, (l, r) -> lt(l,r), Builtin.BOOL), false);
    }

    @Test
    public void testComparisonsOnLongs() throws Exception {
        apply(() -> TestOperators.binaryOperator(0L, 1L, (l, r) -> gte(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(1L, 1L, (l, r) -> gte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(2L, 1L, (l, r) -> gte(l,r), Builtin.BOOL), true);

        apply(() -> TestOperators.binaryOperator(0L, 1L, (l, r) -> gt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(1L, 1L, (l, r) -> gt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(2L, 1L, (l, r) -> gt(l,r), Builtin.BOOL), true);

        apply(() -> TestOperators.binaryOperator(0L, 1L, (l, r) -> lte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(1L, 1L, (l, r) -> lte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(2L, 1L, (l, r) -> lte(l,r), Builtin.BOOL), false);

        apply(() -> TestOperators.binaryOperator(0, 1, (l, r) -> lt(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(1, 1, (l, r) -> lt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(2, 1, (l, r) -> lt(l,r), Builtin.BOOL), false);
    }

    @Test
    public void testComparisonsFloats() throws Exception {
        apply(() -> TestOperators.binaryOperator(0.0F, 1.0F, (l, r) -> gte(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(1.0F, 1.0F, (l, r) -> gte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(2.0F, 1.0F, (l, r) -> gte(l,r), Builtin.BOOL), true);

        apply(() -> TestOperators.binaryOperator(0.0F, 1.0F, (l, r) -> gt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(1.0F, 1.0F, (l, r) -> gt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(2.0F, 1.0F, (l, r) -> gt(l,r), Builtin.BOOL), true);

        apply(() -> TestOperators.binaryOperator(0.0F, 1.0F, (l, r) -> lte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(1.0F, 1.0F, (l, r) -> lte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(2.0F, 1.0F, (l, r) -> lte(l,r), Builtin.BOOL), false);

        apply(() -> TestOperators.binaryOperator(0.0F, 1.0F, (l, r) -> lt(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(1.0F, 1.0F, (l, r) -> lt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(2.0F, 1.0F, (l, r) -> lt(l,r), Builtin.BOOL), false);
    }

    @Test
    public void testComparisonsDoubles() throws Exception {
        apply(() -> TestOperators.binaryOperator(0.0, 1.0, (l, r) -> gte(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(1.0, 1.0, (l, r) -> gte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(2.0, 1.0, (l, r) -> gte(l,r), Builtin.BOOL), true);

        apply(() -> TestOperators.binaryOperator(0.0, 1.0, (l, r) -> gt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(1.0, 1.0, (l, r) -> gt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(2.0, 1.0, (l, r) -> gt(l,r), Builtin.BOOL), true);

        apply(() -> TestOperators.binaryOperator(0.0, 1.0, (l, r) -> lte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(1.0, 1.0, (l, r) -> lte(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(2.0, 1.0, (l, r) -> lte(l,r), Builtin.BOOL), false);

        apply(() -> TestOperators.binaryOperator(0.0, 1.0, (l, r) -> lt(l,r), Builtin.BOOL), true);
        apply(() -> TestOperators.binaryOperator(1.0, 1.0, (l, r) -> lt(l,r), Builtin.BOOL), false);
        apply(() -> TestOperators.binaryOperator(2.0, 1.0, (l, r) -> lt(l,r), Builtin.BOOL), false);
    }

    @Test
    public void testNotAppliedToTrue() throws Exception {
        apply(TestOperators::testNotAppliedToTrue, false);
    }

    @Test
    public void testNotAppliedToFalse() throws Exception {
        apply(TestOperators::testNotAppliedToFalse, true);
    }

    @Test
    public void testAndReturningTrue() throws Exception {
        apply(TestOperators::andReturningTrue, true);
    }

    @Test
    public void testAndReturningFalse() throws Exception {
        apply(TestOperators::andReturningFalse, false);
    }

    @Test
    public void testOrReturningTrue() throws Exception {
        apply(TestOperators::orReturningTrue, true);
    }

    @Test
    public void testOrReturningFalse() throws Exception {
        apply(TestOperators::orReturningFalse, false);
    }

    @Test
    public void testAndWithSecondTermThrowingException() throws Exception {
        apply(TestOperators::andWithSecondTermThrowingException, false);
    }

    @Test
    public void testOrWithSecondTermThrowingException() throws Exception {
        apply(TestOperators::orWithSecondTermThrowingException, true);
    }

    @Test
    public void testIntModulus() throws Exception {
        apply(() -> TestOperators.intModulus(5, 2), 1);
    }

    @Test
    public void testTrivialLoop() throws Exception {
        apply(TestMethods::trivialLoop, 5);
    }

    @Test
    public void testLoopWithSkip() throws Exception {
        apply(TestMethods::loopWithSkip, 5);
    }

    @Test
    public void testLoopWithEscape() throws Exception {
        apply(TestMethods::loopWithEscape, 2);
    }

    @Test
    public void testStaticCall() throws Exception {
        apply(TestMethods::staticCall, 0);
    }

    @Test
    public void testStaticCallUsingClassAsArgument() throws Exception {
        apply(TestMethods::staticCallUsingClassAsArgument, 0);
    }

    @Test
    public void testIntCasts() throws Exception {
        apply(() -> TestCastMethods.castIntMethod(Builtin.I), 1);
        apply(() -> TestCastMethods.castIntMethod(Builtin.F), 1.0f);
        apply(() -> TestCastMethods.castIntMethod(Builtin.D), 1.0d);
        apply(() -> TestCastMethods.castIntMethod(Builtin.L), 1L);
    }

    @Test
    public void testFloatCasts() throws Exception {
        apply(() -> TestCastMethods.castFloatMethod(Builtin.I), 1);
        apply(() -> TestCastMethods.castFloatMethod(Builtin.F), 1.3f);
        // cast floating to double introduces some error
        var d = call(TestCastMethods.castFloatMethod(Builtin.D));
        assertEquals(1.3d, (Double) d, .00001d);
        apply(() -> TestCastMethods.castFloatMethod(Builtin.L), 1L);
    }

    @Test
    public void testLongCasts() throws Exception {
        apply(() -> TestCastMethods.castLongMethod(Builtin.I), Integer.MIN_VALUE);
        apply(() -> TestCastMethods.castLongMethod(Builtin.F), (float) (1L + Integer.MAX_VALUE));
        apply(() -> TestCastMethods.castLongMethod(Builtin.D), (double) (Integer.MAX_VALUE + 1L));
        apply(() -> TestCastMethods.castLongMethod(Builtin.L), 1L + Integer.MAX_VALUE);
    }

    @Test
    public void testDoubleCasts() throws Exception {
        apply(() -> TestCastMethods.castDoubleMethod(Builtin.I), 23);
        apply(() -> TestCastMethods.castDoubleMethod(Builtin.F), 23.4f);
        apply(() -> TestCastMethods.castDoubleMethod(Builtin.D), 23.4d);
        apply(() -> TestCastMethods.castDoubleMethod(Builtin.L), 23L);
    }

    @Test
    public void testNestedLoop() throws Exception {
        apply(TestMethods::nestedLoop, 64);
    }

    @Test
    public void testDenseIntegerSwitchMethod() throws Exception {
        apply(TestMethods::denseIntegerSwitchMethod, 2);
    }

    @Test
    public void testDenseIntegerSwitchMethodWithAssignments() throws Exception {
        apply(TestMethods::denseIntegerSwitchMethodWithAssignments, 5);
    }

    @Test
    public void testConditionalWithSwitchInOrElse() throws Exception {
        apply(TestMethods::conditionalWithSwitchInOrElse, 1);
    }

    @Test
    public void testConditionalWithSwitchAndOrElse() throws Exception {
        apply(TestMethods::conditionalWithSwitchAndOrElse, -1);
    }

    @Test
    public void testDFALoopThingy() throws Exception {
        apply(-1, TestMethods::loopDFAThingy, List.of("abc"));
        apply(0, TestMethods::loopDFAThingy, List.of("0"));
        apply(1, TestMethods::loopDFAThingy, List.of("1"));
    }

    @Test
    public void testLoopWithAndInCondition() throws Exception {
        apply(TestMethods::loopWithAndInCondition, 5);
    }

    @Test
    public void testCallNoArgMethod() throws Exception {
        var return0 = new Method("return0", List.of(), "I", null);
        return0.returnValue(1);
        apply(1, TestMethods::callNoArgMethod, List.of(), return0);
    }

    @Test
    public void testArraySetAndRead() throws Exception {
        apply(TestMethods::arraySetAndGet, 2);
    }

    @Test
    public void testConditional() throws Exception {
        apply(TestMethods::conditional, 3);
    }

    @Test
    public void testNestedConditional() throws Exception {
        apply(TestMethods::nestedConditional, 3);
    }

    @Test
    public void testConditionalWithElse() throws Exception {
        apply(TestMethods::conditionWithElse, 4);
    }

    @Test
    public void testConditionalWithElseIf() throws Exception {
        apply(TestMethods::conditionWithElseIf, 9);
    }

    @Test
    public void testConditionalNonEqWithElse() throws Exception {
        apply(TestMethods::conditionalNonEqWithElse, 3);
    }

    @Test
    public void testNegatedConditional() throws Exception {
        apply(TestMethods::negatedConditional, 4);
    }

    @Test
    public void testTwoSequentialConditionals() throws Exception {
        apply(TestMethods::twoSequentialConditionals, 3);
    }

    @Test
    public void testThreeSequentialConditionals() throws Exception {
        apply(TestMethods::threeSequentialConditionals, 3);
    }

    @Test
    public void testCallOneArgMethod() throws Exception {
        var return0 = new Method("return0", List.of("I"), "I", null);
        return0.returnValue(1);
        apply(1, TestMethods::callOneArgMethod, List.of(), return0);
    }

    @Test
    public void testTwoArgCall() throws Exception {
        var return0 = new Method("return0", List.of("I", "I"), "I", null);
        return0.returnValue(1);
        apply(1, TestMethods::callTwoArgMethod, List.of(), return0);
    }

    @Test
    public void testCallInterfaceMethod() throws Exception {
        var builder = new ClassBuilder("TestSemanticsTestClass" + classNumber++, "", "java/lang/Object", new String[]{});
        builder.addMethod(TestMethods.callInterfaceMethod());
        builder.addMethod(builder.addEmptyConstructor());
        var cls = new ClassCompiler(builder);
        Class<?> compiled = cls.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        var output = compiled.getMethod(TEST_METHOD, List.of(TemporalAccessor.class).toArray(new Class[0])).invoke(instance, Instant.now());
        assertTrue(0 < (Integer) output && 1000 > (Integer) output);
    }

    @Test
    public void testFibonacci() throws Exception {
        var builder = new ClassBuilder("TestSemanticsTestClass" + classNumber++, "", "java/lang/Object", new String[]{});
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
        var builder = new ClassBuilder("TestSemanticsTestClass" + classNumber++, "", "java/lang/Object", new String[]{});
        builder.addMethod(TestMethods.argumentOfReferenceType());
        builder.addMethod(builder.addEmptyConstructor());
        var classCompiler = new ClassCompiler(builder);
        Class<?> compiled = classCompiler.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        var output = compiled.getMethod(TEST_METHOD, List.of(String.class).toArray(new Class[0])).invoke(instance, "abc");
        assertEquals("abc", output);
    }

    @Test
    public void testCreatingClassInPackage() throws Exception {
        var builder = TestClasses.classInPackage("TestSemanticsPackageClass", "com/justinblank/classcompiler/examples");
        var classCompiler = new ClassCompiler(builder);
        Class<?> compiled = classCompiler.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        assertEquals("com.justinblank.classcompiler.examples.TestSemanticsPackageClass", instance.getClass().getCanonicalName());
        assertEquals("com.justinblank.classcompiler.examples", instance.getClass().getPackageName());
    }

    @Test
    public void testCreatingClassInPackageWithDot() throws Exception {
        var builder = TestClasses.classInPackage("TestSemanticsPackageClass2", "com.justinblank.classcompiler.examples");
        var classCompiler = new ClassCompiler(builder);
        Class<?> compiled = classCompiler.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        assertEquals("com.justinblank.classcompiler.examples.TestSemanticsPackageClass2", instance.getClass().getCanonicalName());
        assertEquals("com.justinblank.classcompiler.examples", instance.getClass().getPackageName());
    }

    @Test
    public void testClassInPackageWithStaticAccess() throws Exception {
        var builder = TestClasses.classInPackageWithStaticAccess("TestSemanticsPackageClass3", "com/justinblank/classcompiler/examples");
        var classCompiler = new ClassCompiler(builder);
        Class<?> compiled = classCompiler.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        assertEquals("-", instance.getClass().getDeclaredMethod("returnString").invoke(instance));
    }

    /**
     * Test a zero-argument method by compiling it, and calling it and comparing the actual results with the expected
     * @param method a supplier for the method, must return distinct instances of the method on subsequent calls
     * @param expected the expected value of calling the method
     * @throws Exception
     */
    static void apply(Supplier<Method> method, Object expected) throws Exception {
        Object output = call(method.get(), false);
        Class c = expected.getClass();
        // TODO: improve comparison
        if (c.getName().startsWith("[")) {
            assertEquals(c, output.getClass());
        }
        else {
            assertEquals(expected, output);
        }

        output = call(method.get(), true);
        // TODO: improve comparison
        if (c.getName().startsWith("[")) {
            assertEquals(c, output.getClass());
        }
        else {
            assertEquals(expected, output);
        }
    }

    private static Object call(Method method)  throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        return call(method, false);
    }

    private static Object call(Method method, boolean debug) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        var builder = new ClassBuilder("TestSemanticsTestClass" + classNumber++, "", "java/lang/Object", new String[]{});
        builder.addMethod(method);
        builder.addMethod(builder.addEmptyConstructor());

        var cls = new ClassCompiler(builder, debug, new NoOpPrintStream());
        Class<?> compiled = cls.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        return compiled.getMethod(TEST_METHOD).invoke(instance);
    }

    /**
     * Test a method by compiling it, calling it with the passed arguments and comparing the actual results with the
     * expected
     * @param expected the expected output
     * @param method a supplier for the method, must return distinct instances of the method on subsequent calls
     * @param arguments the arguments to pass to the method
     * @param methods a list of methods that are to be added to the compiled class
     * @throws Exception
     */
    static void apply(Object expected, Supplier<Method> method, List<Object> arguments, Method...methods) throws Exception {
        Object output = call(method.get(), arguments, methods, false);
        assertEquals(expected, output);
        output = call(method.get(), arguments, methods, true);
        assertEquals(expected, output);
    }

    private static Object call(Method method, List<Object> arguments, Method[] methods, boolean debug) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        var builder = new ClassBuilder("TestSemanticsTestClass" + classNumber++, "", "java/lang/Object", new String[]{});
        builder.addMethod(method);
        for (var otherMethod : methods) {
            builder.addMethod(otherMethod);
        }
        builder.addMethod(builder.addEmptyConstructor());
        var cls = new ClassCompiler(builder, debug, new NoOpPrintStream());
        Class<?> compiled = cls.generateClass();
        var instance = compiled.getConstructors()[0].newInstance();
        List<Class<?>> clsArgs = new ArrayList<>();
        for (var obj : arguments) {
            clsArgs.add(obj.getClass());
        }
        return compiled.getMethod(TEST_METHOD, clsArgs.toArray(new Class[0])).invoke(instance, arguments.toArray());
    }
}
