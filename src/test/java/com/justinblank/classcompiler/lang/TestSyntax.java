package com.justinblank.classcompiler.lang;

import com.justinblank.classcompiler.*;
import org.junit.Test;

import java.util.List;

import static com.justinblank.classcompiler.lang.ArrayRead.arrayRead;
import static com.justinblank.classcompiler.lang.CodeElement.*;
import static com.justinblank.classcompiler.lang.CodeElement.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestSyntax {

    private static int classNumber = 0;

    @Test
    public void testVoidNoOpMethod() throws Exception {
        apply(TestMethods.noOpVoidMethod());
    }

    @Test
    public void testCallingVoidMethod() throws Exception {
        apply(TestMethods.callingVoidMethod());
    }

    @Test
    public void testReturnLiteral() throws Exception {
        apply(TestMethods.returnLiteral(Builtin.I, 1));
        apply(TestMethods.returnLiteral(Builtin.I, Integer.MIN_VALUE));
        apply(TestMethods.returnLiteral(Builtin.I, Integer.MAX_VALUE));

        apply(TestMethods.returnLiteral(Builtin.BOOL, 0));
        apply(TestMethods.returnLiteral(Builtin.BOOL, 1));

        apply(TestMethods.returnLiteral(Builtin.C, (int) Character.MIN_VALUE));
        apply(TestMethods.returnLiteral(Builtin.C, (int) Character.MAX_VALUE));

        apply(TestMethods.returnLiteral(Builtin.OCTET, 1));
        apply(TestMethods.returnLiteral(Builtin.OCTET, 127));

        apply(TestMethods.returnLiteral(Builtin.L, 1L));
        apply(TestMethods.returnLiteral(Builtin.L, Long.MIN_VALUE));
        apply(TestMethods.returnLiteral(Builtin.L, Long.MAX_VALUE));

        apply(TestMethods.returnLiteral(Builtin.F, 1f));
        apply(TestMethods.returnLiteral(Builtin.F, Float.MIN_VALUE));
        apply(TestMethods.returnLiteral(Builtin.F, Float.MAX_VALUE));

        apply(TestMethods.returnLiteral(Builtin.D, 1d));
        apply(TestMethods.returnLiteral(Builtin.D, Double.MIN_VALUE));
        apply(TestMethods.returnLiteral(Builtin.D, Double.MAX_VALUE));
    }

    @Test
    public void testMultiReturn() throws Exception {
        apply(TestMethods.multiReturn());
    }

    @Test
    public void testReturnLong() throws Exception {
        apply(TestMethods.returnLong());
    }

    @Test
    public void testReturnChar() throws Exception {
        apply(TestMethods.returnChar());
    }

    @Test
    public void testReturnCharFromMethodCall() throws Exception {
        apply(TestMethods.testCallMethodReturningChar());
    }

    @Test
    public void testCallMethodAcceptingChar() throws Exception {
        apply(TestMethods.testMethodAcceptingChar());
    }

    @Test
    public void testReturningCharAfterStoringInLocalVariable() throws Exception {
        apply(TestMethods.testCallMethodReturningCharAfterStoringInLocalVariable());
    }

    @Test
    public void testNoArgConstructor() throws Exception {
        apply(TestMethods.returnNewDate());
    }

    @Test
    public void testNoOpStatement() throws Exception {
        apply(TestMethods.noOpStatement());
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
    public void testArrayFieldReadAndReadFromLocalVariable() throws Exception {
        apply(TestMethods.arrayFieldReadAndReadFromLocalVariable());
    }

    @Test
    public void testArrayReadUsingIndexFromByteArray() throws Exception {
        apply(TestMethods.arrayReadUsingIndexFromByteArray());
    }

    @Test
    public void testSetAndReadCharVars() throws Exception {
        apply(TestMethods.setAndGetCharVariable());
    }

    @Test
    public void testSetAndReadMultipleVars() throws Exception {
        apply(TestMethods.setAndGetMultipleVariables());
    }

    @Test
    public void testAddition() throws Exception {
        apply(TestOperators.addition());
    }

    @Test
    public void testFloatAddition() throws Exception {
        apply(TestOperators.floatAddition());
    }

    @Test
    public void testEquality() throws Exception {
        apply(TestOperators.equality());
    }

    @Test
    public void testReferenceEquality() throws Exception {
        apply(TestOperators.referenceEquality());
    }

    @Test
    public void testReferenceInquality() throws Exception {
        apply(TestOperators.referenceInequality());
    }

    @Test
    public void testNotAppliedToTrue() throws Exception {
        apply(TestOperators.testNotAppliedToTrue());
    }

    @Test
    public void testNotAppliedToFalse() throws Exception {
        apply(TestOperators.testNotAppliedToFalse());
    }

    @Test
    public void testAndReturningTrue() throws Exception {
        apply(TestOperators.andReturningTrue());
    }

    @Test
    public void testAndReturningFalse() throws Exception {
        apply(TestOperators.andReturningFalse());
    }

    @Test
    public void testOrReturningTrue() throws Exception {
        apply(TestOperators.orReturningTrue());
    }

    @Test
    public void testOrReturningFalse() throws Exception {
        apply(TestOperators.orReturningFalse());
    }

    @Test
    public void testModulus() throws Exception {
        apply(TestOperators.intModulus(5, 2));
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
    public void testSetField() throws Exception {
        apply(TestMethods.setField());
    }

    @Test
    public void testReadStatic() throws Exception {
        apply(TestMethods.readStatic());
    }

    @Test
    public void testArrayLength() throws Exception {
        apply(TestMethods.arrayLength(Builtin.I));
        apply(TestMethods.arrayLength(Builtin.F));
        apply(TestMethods.arrayLength(Builtin.L));
        apply(TestMethods.arrayLength(Builtin.D));
        apply(TestMethods.arrayLength(Builtin.BOOL));
        apply(TestMethods.arrayLength(Builtin.OCTET));
    }

    @Test
    public void testNestedLoop() throws Exception {
        apply(TestMethods.nestedLoop());
    }

    @Test
    public void testDenseIntegerSwitchMethod() throws Exception {
        apply(TestMethods.denseIntegerSwitchMethod());
    }

    @Test
    public void testDenseIntegerSwitchMethodWithAssignments() throws Exception {
        apply(TestMethods.denseIntegerSwitchMethodWithAssignments());
    }

    @Test
    public void testConditionalWithSwitchAndOrElse() throws Exception {
        apply(TestMethods.conditionalWithSwitchAndOrElse());
    }

    @Test
    public void testConditionalWithSwitchInOrElse() throws Exception {
        apply(TestMethods.conditionalWithSwitchInOrElse());
    }

    @Test
    public void testLoopWithAndInCondition() throws Exception {
        apply(TestMethods.loopWithAndInCondition());
    }

    @Test
    public void testDFAThingy() throws Exception {
        apply(TestMethods.loopDFAThingy());
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
    public void testNestedConditional() throws Exception {
        apply(TestMethods.nestedConditional());
    }

    @Test
    public void testConditionalWithElse() throws Exception {
        apply(TestMethods.conditionWithElse());
    }

    @Test
    public void testConditionalWithElseIf() throws Exception {
        apply(TestMethods.conditionWithElseIf());
    }

    @Test
    public void testConditionalNonEqWithElse() throws Exception {
        apply(TestMethods.conditionalNonEqWithElse());
    }

    @Test
    public void testNegatedConditional() throws Exception {
        apply(TestMethods.negatedConditional());
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
        apply(TestCastMethods.castIntMethod(Builtin.S));
        apply(TestCastMethods.castIntMethod(Builtin.F));
        apply(TestCastMethods.castIntMethod(Builtin.D));
        apply(TestCastMethods.castIntMethod(Builtin.L));
    }

    @Test
    public void testShortCasts() throws Exception {
        apply(TestCastMethods.castShortMethod(Builtin.I));
        apply(TestCastMethods.castShortMethod(Builtin.S));
        apply(TestCastMethods.castShortMethod(Builtin.F));
        apply(TestCastMethods.castShortMethod(Builtin.D));
        apply(TestCastMethods.castShortMethod(Builtin.L));
    }

    @Test
    public void testFloatCasts() throws Exception {
        apply(TestCastMethods.castFloatMethod(Builtin.I));
        apply(TestCastMethods.castShortMethod(Builtin.S));
        apply(TestCastMethods.castFloatMethod(Builtin.F));
        apply(TestCastMethods.castFloatMethod(Builtin.D));
        apply(TestCastMethods.castFloatMethod(Builtin.L));
    }

    @Test
    public void testLongCasts() throws Exception {
        apply(TestCastMethods.castLongMethod(Builtin.I));
        apply(TestCastMethods.castLongMethod(Builtin.S));
        apply(TestCastMethods.castLongMethod(Builtin.F));
        apply(TestCastMethods.castLongMethod(Builtin.D));
        apply(TestCastMethods.castLongMethod(Builtin.L));
    }

    @Test
    public void testDoubleCasts() throws Exception {
        apply(TestCastMethods.castDoubleMethod(Builtin.I));
        apply(TestCastMethods.castDoubleMethod(Builtin.S));
        apply(TestCastMethods.castDoubleMethod(Builtin.F));
        apply(TestCastMethods.castDoubleMethod(Builtin.D));
        apply(TestCastMethods.castDoubleMethod(Builtin.L));
    }

    @Test
    public void testClassInPackage() throws Exception {
        apply(TestClasses.classInPackage("TestClassInPackageForTestSyntax", "com/justinblank/classcompiler"));
    }

    @Test
    public void testClassInPackageWithDot() throws Exception {
        apply(TestClasses.classInPackage("TestClassInPackageForTestSyntax2", "com.justinblank.classcompiler"));
    }

    @Test
    public void testClassInPackageWithStaticAccess() throws Exception {
        apply(TestClasses.classInPackageWithStaticAccess("TestClassInPackageForTestSyntax3", "com/justinblank/classcompiler"));
    }

    @Test
    public void testTypeInferenceFailure() throws Exception {
        var method = new Method(TestMethods.TEST_METHOD, List.of(), ReferenceType.of(String.class), new GenericVars("a"));
        method.set("a", 1);
        method.set("a", call("toString", String.class, CodeElement.construct(StringBuilder.class)));
        method.returnValue(read("a"));
        assertThrows(ClassCompilationException.class, () -> {
            apply(method);
        });
    }

    static void apply(ClassBuilder classBuilder) throws Exception {
        var clsBuilder = new ClassCompiler(classBuilder);
        Class<?> compiled = clsBuilder.generateClass();
        compiled.getConstructors()[0].newInstance();
    }

    static void apply(Method method) throws Exception {
        apply("TestSyntaxTestClass" + classNumber++, method);
    }

    static void apply(String className, Method method) throws Exception {
        var builder = new ClassBuilder(className, "", "java/lang/Object", new String[]{});
        builder.addMethod(method);
        builder.addMethod(builder.addEmptyConstructor());

        var classCompiler = new ClassCompiler(builder);
        Class<?> compiled = classCompiler.generateClass();
        compiled.getConstructors()[0].newInstance();
    }

    static void apply(Method... methods) throws Exception {
        apply("TestSyntaxTestClass" + classNumber++, methods);
    }

    static void apply(String className, Method...methods) throws Exception {
        var builder = new ClassBuilder(className, "", "java/lang/Object", new String[]{});
        for (var method : methods) {
            builder.addMethod(method);
        }
        builder.addMethod(builder.addEmptyConstructor());
        var classCompiler = new ClassCompiler(builder);
        Class<?> compiled = classCompiler.generateClass();
        compiled.getConstructors()[0].newInstance();
    }
}
