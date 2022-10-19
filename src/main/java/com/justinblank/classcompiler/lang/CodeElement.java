package com.justinblank.classcompiler.lang;

// Interface representing an expression or statements

import com.justinblank.classcompiler.CompilerUtil;

import java.util.List;

import static com.justinblank.classcompiler.lang.Literal.literal;

public interface CodeElement {

    static Conditional cond(Expression expression) {
        return new Conditional(expression);
    }

    static Expression call(String methodName, Type returnType, Expression... arguments) {
        return new Call(null, methodName, returnType, false, false, false, arguments);
    }

    static Expression call(String methodName, Class<?> returnType, Expression... arguments) {
        return new Call(null, methodName, ReferenceType.of(returnType), false, false, false, arguments);
    }

    static Expression callInterface(String methodName, Type returnType, Expression... arguments) {
        return new Call(null, methodName, returnType, false, false, true, arguments);
    }

    static Expression callInterface(String methodName, Class<?> returnType, Expression... arguments) {
        return new Call(null, methodName, ReferenceType.of(returnType), false, false, true, arguments);
    }

    static Expression callStatic(String className, String methodName, Type returnType, Expression... arguments) {
        return new Call(className, methodName, returnType, true, false, false, arguments);
    }

    static Expression callStatic(String className, String methodName, Class<?> returnType, Expression... arguments) {
        return new Call(className, methodName, ReferenceType.of(returnType), true, false, false, arguments);
    }

    static Expression callStatic(Class<?> cls, String methodName, Type returnType, Expression... arguments) {
        return new Call(CompilerUtil.internalName(cls), methodName, returnType, true, false, false, arguments);
    }

    static Expression cast(Type outputType, Expression value) {
        return new Cast(outputType, value);
    }

    static Constructor construct(Type type, Expression...arguments) {
        return new Constructor(type, arguments);
    }

    static Constructor construct(Class<?> type, Expression...arguments) {
        return new Constructor(ReferenceType.of(type), arguments);
    }

    static ArrayLength arrayLength(Expression expression) {
        return new ArrayLength(expression);
    }

    /**
     * Access a local variable by name
     * @param variable the variable name
     * @return an Expression representing a variable read
     */
    static Expression read(String variable) {
        return new VariableRead(variable);
    }

    /**
     * Create a fieldReference
     * @param fieldName the field name
     * @param fieldType the type of the field being referenced
     * @param objectReference the target of the field access (e.g. `X` in `X.i`).
     * @return a fieldReference
     */
    static FieldReference get(String fieldName, Type fieldType, Expression objectReference) {
        return new FieldReference(fieldName, fieldType, objectReference);
    }

    static FieldReference get(String fieldName, Class<?> fieldType, Expression objectReference) {
        return new FieldReference(fieldName, ReferenceType.of(fieldType), objectReference);
    }

    static StaticFieldReference getStatic(String fieldName, Type classType, Type fieldType) {
        return new StaticFieldReference(fieldName, classType, fieldType);
    }

    static FieldSet fieldSet(FieldReference fieldReference, Expression expression) {
        return new FieldSet(fieldReference, expression);
    }

    static Statement set(String name, Expression value) {
        return new Assignment(name, value);
    }

    static Statement set(String name, Number value) {
        return new Assignment(name, literal(value));
    }

    static Escape escape() {
        return new Escape();
    }

    static Skip skip() {
        return new Skip();
    }

    static Statement returnValue(Expression expression) {
        return new ReturnExpression(expression);
    }

    static Statement returnValue(Number number) {
        return new ReturnExpression(literal(number));
    }

    static Statement returnVoid() {
        return new ReturnVoid();
    }


    static Expression thisRef() {
        return new ThisRef();
    }

    static Loop loop(Expression condition, List<CodeElement> body) {
        return new Loop(condition, body);
    }
}
